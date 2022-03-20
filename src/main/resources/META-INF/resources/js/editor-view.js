class EditorView {
    constructor(controller = new EditorController()) {
        this.controller = controller;
    }

    async initEditor(editorId = 'md-editor') {
        const urlSearchParams = new URLSearchParams(window.location.search);
        const params = Object.fromEntries(urlSearchParams.entries());

        if (params.id) {
            await this.controller.selectById(params.id);
        } else if (params.templateId) {
            await this.controller.selectByTemplateId(params.templateId);
        }

        const easyMDE = new EasyMDE({
            element: document.getElementById(editorId),
            toolbar: this.createToolbar(),
            shortcuts: {
                "save": "Ctrl-S"
            }
        });
        easyMDE.value(this.controller.selected?.content || '');
        if (!easyMDE.isSideBySideActive()) easyMDE.toggleSideBySide();
    }

    createToolbar() {
        const toolbar = new EasyMDE({ showIcons: ["code", "table", "redo", "undo"] }).toolbar;
        return [
            {
                name: "save",
                action: async editor => {
                    const  fileName = window.prompt("Filename:", this.controller.selected.name);
                    if(fileName) {
                        await this.controller.save(fileName, editor.value());
                        // update the URL without reloading the page
                        var newurl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?id=' + this.controller.selected.id;
                        window.history.pushState({path:newurl},'',newurl);
                    }
                },
                className: "fa fa-save",
                title: "Save",
            },{
                name: "clear",
                action: editor => editor.value(""),
                className: "fa fa-remove",
                title: "Clear",
            },{
                name: "home",
                action: editor => document.location.href = "/",
                className: "fa fa-home",
                title: "Home",
            },
            '|',
            ...toolbar
        ];
    }
}