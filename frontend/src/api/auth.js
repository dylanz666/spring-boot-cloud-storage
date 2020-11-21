import request from '@/utils/request'

export function getAuth() {
    return request({
        url: '/api/auth',
        method: 'get',
        params: {}
    })
}

export function logout() {
    return request({
        url: '/logout',
        method: 'get',
        params: {}
    })
}
