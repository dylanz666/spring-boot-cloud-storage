import request from '@/utils/request'

export function getSpaceInfo() {
    return request({
        url: '/api/space',
        method: 'get'
    });
}