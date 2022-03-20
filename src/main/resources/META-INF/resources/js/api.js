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
        }0
        this.selected = await this.service.save(this.selected);
    }
}

class FileListController {
    constructor(service = new FileService()) {
        this.service = service;
    }

    async getAllFiles(query, tags) {
        return this.service.findAll(query, tags);
    }

    async deleteOne(id) {
        this.service.deleteOne(id);
    }
}


class LoginController {
    constructor(client = new LoginClient()) {
        this.client = client;
    }

    async login(username, password) {
        return this.client.login(username, password);
    }

    async logout() {
        return this.client.logout();
    }

    async isLoggeding() {
        return this.client.isLoggeding();
    }
}

/* SERVICE */

class FileService {
    constructor(client = new FileClient()) {
        this.client = client;
    }

    async findAll(query, tags) {
        return this.client.findAll(query, tags);
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

    async findAll(query = '', tags = []) {
        tags = tags.map(tag => encodeURIComponent(tag)).join(",")
        tags = tags ? '&tags=' + tags : '';
        const response = await fetch(this.baseUrl + '/api/files?query=' + encodeURIComponent(query) + tags);
        ResponseHandler.handleResponse(response);
        return response.json();
    }

    async findOne(id) {
        const response = await fetch(this.baseUrl + '/api/files/' + id);
        ResponseHandler.handleResponse(response);
        return response.json();
    }

    async deleteOne(id) {
        const response = await fetch(this.baseUrl + '/api/files/' + id, { method: "DELETE"});
        ResponseHandler.handleResponse(response);
        return response;
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
        ResponseHandler.handleResponse(response);
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
        ResponseHandler.handleResponse(response);
        return response.json();
    }

}

class ResponseHandler {

    static handleResponse(response) {
        ResponseHandler.handle401(response);
    }

    static handle401(response) {
        if (response.status === 401) {
            document.location.href = "/login.html";
        }
    }
}

/* CLIENT */

class LoginClient {
    constructor(baseUrl = '') {
        this.baseUrl = baseUrl;
    }

    async  login(login, password) {
         const response = await fetch(
             this.baseUrl + '/api/auth/login?login=' + encodeURIComponent(login) + '&password=' + encodeURIComponent(password),
             {
                 method: 'POST'
             })
         return response.json();
     }

    async logout() {
         const response = await fetch(
             this.baseUrl + '/api/auth/logout',
             {
                 method: 'POST'
             })
         return response.json();
     }

    async refresh() {
         const response = await fetch(
             this.baseUrl + '/api/auth/refresh',
             {
                 method: 'POST'
             })
         return response.json();
     }

    async isLoggedin() {
        try {
            const response = await fetch(this.baseUrl + '/api/auth/account');
            ResponseHandler.handleResponse(response);
            return response;
        } catch(ex) {
            return false;
        }
    }
}

const tryRefresh = async () => {
    try {
        await new LoginClient().refresh();
        return true;
    } catch (e) {
        console.warn("refresh failed, logging out");
        try {
            await new LoginClient().logout();
        } catch (e) {
            console.warn("could not logout");
        }
        return false;
    }
}

tryRefresh().then(async successful => {
    if (successful) {
        setInterval(async () => {
            await tryRefresh();
        }, 3 * 60000);
    }
});