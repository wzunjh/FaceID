import axios from "axios";
import Element from "element-ui"

axios.defaults.baseURL = "http://localhost:8868"

const request = axios.create({
    timeout: 15000,
    headers: {
        'Content-Type': "application/json; charset=utf-8"
    }
})

request.interceptors.request.use(config => {
    config.headers['face_token'] = localStorage.getItem("face_token")
    return config
})

request.interceptors.response.use(
    response => {
        // 如果响应的状态码是2xx，直接返回响应
        if (response.status >= 200 && response.status < 300) {
            return response;
        }
        // 如果响应中有code字段，再进行额外的检查
        if (response.data && response.data.code) {
            if (response.data.code === 200 || response.data.code === 201){
                return response;
            } else {
                Element.Message.error(response.data.msg, {duration: 3000});
                return Promise.reject(response.data.msg);
            }
        }
        // 如果没有code字段，也返回响应
        return response;
    },
    error => {
        Element.Message.error(error.message, {duration: 3000});
        return Promise.reject(error);
    }
)

export default request