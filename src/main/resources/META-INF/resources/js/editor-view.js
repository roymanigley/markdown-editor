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
                "save": "Ctrl-S",
                "home": "Ctrl-H",
                "upload": "Ctrl-U",
                "editTags": "Ctrl-E",
                "fillPlaceholder": "Ctrl-M",
                "template": "Ctrl-K"
            }
        });
        easyMDE.value(this.controller.selected?.content || '');
        if (!easyMDE.isSideBySideActive()) easyMDE.toggleSideBySide();
    }

    createToolbar() {
        const toolbar = new EasyMDE({ showIcons: ["code", "table", "redo", "undo"] }).toolbar;
        return [
            {
                name: "home",
                action: editor => document.location.href = "/",
                className: "fa fa-home",
                title: "Home",
            },
            '|',
            {
                name: "save",
                action: async editor => {
                    const  fileName = window.prompt("Filename:", this.controller.selected.name);
                    if(fileName) {
                        try {
                            await this.controller.save(fileName, editor.value());
                            if (this.controller.selected.id) {
                                // update the URL without reloading the page
                                var newurl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?id=' + this.controller.selected.id;
                                window.history.pushState({path:newurl},'',newurl);
                                M.toast({html: 'saved'});
                            } else {
                                throw new Error();
                            }
                        } catch (ex) {
                            M.toast({html: 'save failed'});
                        }
                    }
                },
                className: "fa fa-save",
                title: "Save",
            },
            {
                name: "editTags",
                action: async editor => {
                    if (!this.controller.selected.id) {
                        alert("Save the file before tagging");
                        return;
                    }
                    const tags = this.controller.selected.tags?.filter(tag => tag.trim())?.join(", ") || "";
                    let  newTags = window.prompt("Tags:", tags);
                    if (newTags) {
                        newTags = newTags ? newTags.split(/\s*,\s*/) : [];
                        this.controller.selected.tags = newTags;
                        try {
                            await this.controller.save(this.controller.selected.name, editor.value());
                            M.toast({html: 'tagged and updated'});
                        } catch (ex) {
                            M.toast({html: 'tagging failed'});
                        }
                    }
                },
                className: "fa fa-tag",
                title: "edit tags",
            },
            {
              name: "fillPlaceholder",
              action: editor => {
                    let markdown = editor.value();
                    const placeHolders = [...markdown.matchAll(/\$\{[A-Za-z0-9_]+\}/g)]
                        .map(p => p[0]);
                    placeHolders
                        .filter((value, index) => placeHolders.indexOf(value) === index)
                        .forEach(placeHolder => {
                            let defaultValue = placeHolder;
                            console.log(defaultValue)
                            if (defaultValue.toLowerCase().replaceAll("_", "").match("datetime")) {
                                defaultValue = new Date().toISOString().substring(0, 16).replace("T", " ");
                            } else if (defaultValue.toLowerCase().replaceAll("_", "").match("date")) {
                                defaultValue = new Date().toISOString().substring(0, 10);
                            }
                            const  placeholderValue = window.prompt("Placeholder: " + placeHolder, defaultValue);
                            if (placeholderValue) {
                                markdown = markdown.replaceAll(placeHolder, placeholderValue)
                            }
                        });
                    if (placeHolders.length) {
                        markdown = editor.value(markdown);
                        M.toast({html: placeHolders.length +' placeholder found'});
                    } else {
                        M.toast({html: 'no placeholder found, ex: ${PLACE_HOLDER_01}'});
                    }
              },
              className: "fa fa-magic",
              title: "Fill placeholder → ${EXAMPLE_PLACEHOLDER}",
            },
            {
               name: "template",
               action: editor => {
                if (this.controller.selected.id)
                    document.location.href = '/editor.html?templateId=' + this.controller.selected.id
                else {
                    alert("Save the file before coping");
                }
               },
               className: "fa fa-copy",
               title: "Copy content to a new file",
            },
            {
               name: "upload",
               action: editor => window.open('/image-upload.html','_blank'),
               className: "fa fa-upload",
               title: "Upload image",
            },
            '|',
            ...toolbar
        ];
    }
}
