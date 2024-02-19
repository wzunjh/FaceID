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
        if (response.data.code === 200 || response.data.code === 201){
            return response
        }else {
            Element.Message.error(response.data.msg, {duration: 3000})
            return Promise.reject(response.data.msg)
        }
    },
    error => {
        Element.Message.error(error.msg, {duration: 3000})
        return Promise.reject(error)
    }
)

export default request