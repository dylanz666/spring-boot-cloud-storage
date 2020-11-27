import request from '@/utils/request'

export function deleteFiles(files) {
    return request({
        url: '/api/file/batch',
        method: 'delete',
        data: files
    });
}

export function updateFile(folderName, currentFileName, targetFileName) {
    return request({
        url: '/api/file',
        method: 'put',
        data: { folderName, currentFileName, targetFileName }
    });
}

export function download(folderName, fileName) {
    return request({
        url: '/api/file/download',
        method: 'get',
        params: { folderName, fileName }
    });
}

export function getFile(folderName, fileName) {
    return request({
        url: '/api/file',
        method: 'get',
        params: { folderName, fileName }
    });
}