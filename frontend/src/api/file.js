import request from '@/utils/request'

export function createFolder(folderName) {
    return request({
        url: '/api/folder',
        method: 'post',
        params: { folderName }
    });
}

export function deleteFolder(folderName, all) {
    return request({
        url: '/api/folder',
        method: 'delete',
        params: { folderName, all }
    });
}

export function updateFolder(folderInformation) {
    return request({
        url: '/api/folder',
        method: 'put',
        params: { folderName, all }
    });
}