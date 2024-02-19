/**
 * 获取 浏览器 拍照的权限
 */


/**
 * 获取浏览器权限
 * @param option
 */
function getCamera(option) {
    option.thisCancas = document.getElementById(option.canvasId);
    option.thisContext = option.thisCancas.getContext("2d");
    option.thisVideo = document.getElementById(option.videoId);
    option.thisVideo.style.display = "block";
    // 获取媒体属性，旧版本浏览器可能不支持mediaDevices，我们首先设置一个空对象
    if (navigator.mediaDevices === undefined) {
        navigator.mediaDevices = {};
    }
    // 一些浏览器实现了部分mediaDevices，我们不能只分配一个对象
    // 使用getUserMedia，因为它会覆盖现有的属性。
    // 这里，如果缺少getUserMedia属性，就添加它。
    if (navigator.mediaDevices.getUserMedia === undefined) {
        navigator.mediaDevices.getUserMedia = function (constraints) {
            // 首先获取现存的getUserMedia(如果存在)
            var getUserMedia =
                navigator.webkitGetUserMedia ||
                navigator.mozGetUserMedia ||
                navigator.getUserMedia;
            // 有些浏览器不支持，会返回错误信息
            // 保持接口一致
            if (!getUserMedia) {
                //不存在则报错
                return Promise.reject(
                    new Error("getUserMedia is not implemented in this browser")
                );
            }
            // 否则，使用Promise将调用包装到旧的navigator.getUserMedia
            return new Promise(function (resolve, reject) {
                getUserMedia.call(navigator, constraints, resolve, reject);
            });
        };
    }
    var constraints = {
        audio: false,
        video: {
            width: option.videoWidth,
            height: option.videoHeight,
            transform: "scaleX(-1)",
        },
    };
    navigator.mediaDevices
        .getUserMedia(constraints)
        .then(function (stream) {
            // 旧的浏览器可能没有srcObject
            if ("srcObject" in option.thisVideo) {
                option.thisVideo.srcObject = stream;
            } else {
                // 避免在新的浏览器中使用它，因为它正在被弃用。
                option.thisVideo.src = window.URL.createObjectURL(stream);
            }
            option.thisVideo.onloadedmetadata = function (e) {
                option.thisVideo.play();
            };
        })
        .catch((err) => {
            console.log(err);
        });
    return option
}

/**
 * 绘制图片
 * 返回格式为base64
 * @param option
 */
function draw(option){
    option.thisContext.drawImage(option.thisVideo,0,0,option.videoWidth,option.videoHeight);
    return option.thisCancas.toDataURL("image/png");
}

export default {
    getCamera,
    draw
}