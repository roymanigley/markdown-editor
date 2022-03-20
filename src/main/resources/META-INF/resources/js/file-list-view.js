class FileListView {
    constructor(wrapperClass = 'file-list-wrapper', controller = new FileListController()) {
        this.wrapperClass = wrapperClass;
        this.controller = controller;
    }

    async search(query, tags) {
        this.initList(query, tags)
    }

    async deleteOne(id, fileName) {
        if (window.confirm("Do you wan to delete this file?\n" + fileName)) {
            await this.controller.deleteOne(id);
            setTimeout(() => this.initList(''), 100);
        }
    }

    async initList(query, tags) {
        let files = [];
        try {
            files = await this.controller.getAllFiles(query, tags);

        } catch (ex) {
            console.error(ex);
        }
        const ul = document.createElement('ul');
        ul.className = 'collection';

        files.forEach(file => {
            const li = document.createElement('li');
            const tags = file.tags
                .filter(tag => tag.trim())
                .map(tag =>
                        '<div class="chip teal" onclick="selectToEdit(\'' + file.id + '\')">' +
                        tag +
                        '</div>'
                ).join("\n");
            li.className = 'collection-item avatar';
            li.innerHTML = '<i class="material-icons circle" onclick="selectToEdit(\'' + file.id + '\')">folder</i>' +
                        '<span class="title" onclick="selectToEdit(\'' + file.id + '\')">' + file.name + '</span><br>' +
                        tags +
                        '<p class="audit-infos" onclick="selectToEdit(\'' + file.id + '\')">created: ' + file.createDateTime.toString().substring(0,16).replace('T', ' ') + ' by ' + file.creator + '<br>' +
                        'updated: ' + file.editDateTime.toString().substring(0,16).replace('T', ' ') + ' by ' + file.editor +
                        '</p>' +
                        '<span class="secondary-content">' +
                            '<a href="#" onclick="deleteOne(\'' + file.id + '\', \'' + file.name + '\')"><i class="danger-color material-icons">delete</i></a>' +
                            '<a href="editor.html?templateId=' + file.id + '"><i class="primary-color material-icons">content_copy</i></a>' +
                            '<a href="editor.html?id=' + file.id + '"><i class="primary-color material-icons">edit</i></a>' +
                        '</span>';
            ul.append(li);
        });
        const listWrapper = document.getElementById(this.wrapperClass);
        listWrapper.innerHTML = '';
        listWrapper.append(ul);
    }


}