/* CONTROLLER */

class EditorController {
    constructor(service = new FileService()) {
        this.service = service;
        this.selected = new File();
    }

    async selectById(id) { this.selected = await this.service.findOne(id); }

    async selectByTemplateId(templateId) {
        const template = await this.service.findOne(templateId);
        this.selected = new File(template.name + "-" + new Date().toISOString().substring(0, 16), template.content);
    }

    async save (name, content) {
        if (!this.selected?.id) {
            this.selected = new File(name, content);
        } else {
            this.selected.name = name;
            this.selected.content = content;
        }
        this.selected = await this.service.save(this.selected);
    }
}

class FileListController {
    constructor(service = new FileService()) {
        this.service = service;
    }

    async getAllFiles(query) {
        return this.service.findAll(query);
    }

    async deleteOne(id) {
        this.service.deleteOne(id);
    }
}


class LoginController {
    constructor(service = new LoginService()) {
        this.service = service;
    }

    async login(username, password) {
        return {};
    }

    async logout() {
        return {};
    }

    async isLoggeding() {
        this.service.isLoggeding();
    }
}

/* SERVICE */

class FileService {
    constructor(client = new FileClient()) {
        this.client = client;
    }

    async findAll(query) {
        return this.client.findAll(query);
    }

    async findOne(id) {
        return this.client.findOne(id);
    }

    async deleteOne(id) {
        return this.client.deleteOne(id);
    }

    async findOneTemplate(id) {
        return this.client.findOneTemplate(id);
    }

    async save(file) {
        if (file.id) {
            return this.client.update(file);
        } else {
            return this.client.create(file);
        }
    }
}

/* MODEL */

class File {
    constructor(name, content) {
        this.name = name;
        this.content = content;
        this.directory = "/";
        this.tags = [];
    }
    creator;
    createDateTime;
    editor;
    editDateTime;
}
/* JAVA Class

class File {
    private String name;
    private String content;
    private String directory;
    private String creator;
    private List<String> tags;
    private ZonedDateTime createDateTime;
    private String editor;
    private ZonedDateTime editDateTime;
}
*/

/* CLIENT */

class FileClient {
    constructor(baseUrl = '') {
        this.baseUrl = baseUrl;
    }

    async findAll(query = '') {
        const response = await fetch(this.baseUrl + '/api/files?query=' + encodeURIComponent(query));
        return response.json();
    }

    async findOne(id) {
        const response = await fetch(this.baseUrl + '/api/files/' + id);
        return response.json();
    }

    async deleteOne(id) {
        return fetch(this.baseUrl + '/api/files/' + id, { method: "DELETE"});
    }

    async create(file) {
        file.id = null;
        const response = await fetch(
            this.baseUrl + '/api/files',
            {
                method: 'POST',
                headers: {
                  'Content-Type': 'application/json'
                },
                body: JSON.stringify(file)
            });
        return response.json();
    }

    async update(file) {
        const response = await fetch(
            this.baseUrl + '/api/files',
            {
                method: 'PUT',
                headers: {
                  'Content-Type': 'application/json'
                },
                body: JSON.stringify(file)
            });
        return response.json();
    }
}


/* CLIENT */

class LoginClient {
    constructor(baseUrl = '') {
        this.baseUrl = baseUrl;
    }

    async  login(login, password) {
         return fetch(
             this.baseUrl + '/api/auth/login',
             {
                 method: 'POST',
                 body: '?login=' + encodeURIComponent(login) + '&password' + encodeURIComponent(password)
             }).json();
     }

    async logout(login, password) {
         return await fetch(
             this.baseUrl + '/api/auth/logout',
             {
                 method: 'POST'
             }).json();
     }

    async isLoggedin() {
        try {
            return await fetch(this.baseUrl + '/api/auth/account')
        } catch(ex) {
            return false;
        }
    }
}