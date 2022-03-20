class LoginView {
    constructor(loginId = 'login', passwordId = 'password', controller = new LoginController()) {
        this.loginId = loginId;
        this.passwordId = passwordId;
        this.controller = controller;
    }

    async login() {
        const login = document.getElementById(this.loginId)?.value;
        const password = document.getElementById(this.passwordId)?.value;
        try {
            const user = await this.controller.login(login, password);
            document.location.href = '/index.html';
            M.toast({html: 'Welcome ' + user.login});
        } catch (ex) {
            M.toast({html: 'Login failed'});
        }
    }

    async logout() {
        await this.controller.logout();
        document.location.href = '/login.html'
    }
}