import request from '@/utils/request'

export function login(username, password) {
    let form = new FormData();
    form.append("username", username);
    form.append("password", password);
    return request({
        url: '/login',
        method: 'post',
        data: form
    });
}

export function ping() {
    return request({
        url: '/ping',
        method: 'get',
        params: {}
    })
}
