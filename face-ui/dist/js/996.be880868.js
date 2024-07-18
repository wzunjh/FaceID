"use strict";(self["webpackChunkface_id"]=self["webpackChunkface_id"]||[]).push([[996],{88996:(e,t,a)=>{a.r(t),a.d(t,{default:()=>o});var i=function(){var e=this,t=e._self._c;return t("div",[t("el-row",[t("el-col",{attrs:{span:24}},[t("el-card",{attrs:{header:"自建人脸信息库列表"}},[t("el-form",{attrs:{inline:!0}},[t("el-form-item",{attrs:{label:"人脸名称"}},[t("el-input",{attrs:{placeholder:"名称",clearable:""},model:{value:e.searchFace.faceName,callback:function(t){e.$set(e.searchFace,"faceName",t)},expression:"searchFace.faceName"}})],1),t("el-form-item",[t("el-button",{attrs:{icon:"el-icon-search"},on:{click:e.getFaceList}},[e._v("搜索")])],1)],1)],1)],1)],1),t("el-row",[t("el-col",{attrs:{span:24}},[t("el-card",[t("el-header",{staticStyle:{"background-color":"#ffffff"}},[t("el-button",{attrs:{icon:"el-icon-circle-plus-outline"},on:{click:function(t){return e.handleSave()}}},[e._v("添加人脸")])],1),t("el-table",{staticStyle:{width:"100%"},attrs:{data:e.tableData}},[t("el-table-column",{attrs:{width:"100",prop:"faceId",label:"编号",sortable:""}}),t("el-table-column",{attrs:{width:"200",prop:"date",label:"人脸图片"},scopedSlots:e._u([{key:"default",fn:function(a){return[t("el-button",{attrs:{icon:"el-icon-picture-outline",size:"mini"},on:{click:function(t){return e.selectImg(a.row.faceBase)}}},[e._v("查看人脸")])]}}])}),t("el-table-column",{attrs:{width:"200",prop:"faceName",label:"姓名"}}),t("el-table-column",{attrs:{label:"操作",width:"230"},scopedSlots:e._u([{key:"default",fn:function(a){return[t("el-button",{attrs:{icon:"el-icon-edit",size:"mini"},on:{click:function(t){return e.handleEdit(a.$index,a.row)}}},[e._v("编辑")]),t("el-popconfirm",{attrs:{title:"确认删除"+a.row.faceName+"的人脸数据吗?"},on:{confirm:function(t){return e.handleDelete(a.$index,a.row)}}},[t("el-button",{staticStyle:{"margin-left":"20px"},attrs:{slot:"reference",icon:"el-icon-delete",size:"mini",type:"danger"},slot:"reference"},[e._v("删除")])],1)]}}])})],1)],1)],1)],1),t("el-row",{staticClass:"page-row"},[t("el-pagination",{staticClass:"el-pagination",attrs:{layout:"total, sizes, prev, pager, next, jumper","page-sizes":[5,10,30,50],"current-page":e.current,"page-size":e.size,total:e.total},on:{"size-change":e.handleSizeChange,"current-change":e.handleCurrentChange}})],1),t("el-dialog",{attrs:{title:"人脸图片",visible:e.selectImgDialog,width:"30%"},on:{"update:visible":function(t){e.selectImgDialog=t}}},[t("img",{staticStyle:{width:"100%"},attrs:{src:e.currentImg}})]),t("el-dialog",{attrs:{title:e.saveAndUpdateTitle,visible:e.saveAndUpdateDialog,"before-close":e.handleClose,width:"600px"},on:{"update:visible":function(t){e.saveAndUpdateDialog=t}}},[t("el-form",{ref:"ruleForm",attrs:{model:e.faceObj,rules:e.rules,"label-width":"80px"}},[t("input",{directives:[{name:"model",rawName:"v-model",value:e.faceObj.fid,expression:"faceObj.fid"}],attrs:{type:"hidden"},domProps:{value:e.faceObj.fid},on:{input:function(t){t.target.composing||e.$set(e.faceObj,"fid",t.target.value)}}}),t("input",{directives:[{name:"model",rawName:"v-model",value:e.faceObj.faceId,expression:"faceObj.faceId"}],attrs:{type:"hidden"},domProps:{value:e.faceObj.faceId},on:{input:function(t){t.target.composing||e.$set(e.faceObj,"faceId",t.target.value)}}}),t("el-form-item",{attrs:{label:"人脸名称",prop:"faceName"}},[t("el-input",{attrs:{placeholder:"必填项"},model:{value:e.faceObj.faceName,callback:function(t){e.$set(e.faceObj,"faceName",t)},expression:"faceObj.faceName"}})],1),t("el-form-item",{staticClass:"login-mid-mid",attrs:{label:"人脸数据",prop:"faceBase"}},[t("el-upload",{staticClass:"avatar-uploader",attrs:{action:e.fakeUploadUrl,"http-request":e.handleUpload,"before-upload":e.beforeUpload,"show-file-list":!1,"on-remove":e.handleRemove,limit:1,"on-exceed":e.handleExceed}},[e.faceObj.faceBase?t("img",{staticClass:"avatar",attrs:{src:e.faceObj.faceBase}}):t("i",{staticClass:"el-icon-plus avatar-uploader-icon"})])],1),t("el-form-item",[t("el-button",{attrs:{type:"primary"},on:{click:function(t){return e.submitForm("ruleForm")}}},[e._v("立即创建")]),t("el-button",{on:{click:function(t){return e.resetForm("ruleForm")}}},[e._v("重置")])],1)],1)],1)],1)},s=[];a(41539);const n={data:function(){return{size:5,current:1,total:0,searchFace:{faceName:""},tableData:[],selectImgDialog:!1,currentImg:"",saveAndUpdateDialog:!1,saveAndUpdateTitle:"",faceObj:{fid:localStorage.getItem("user_id"),faceId:null,faceName:"",faceBase:"",addAndUpdateState:!1},rules:{faceName:[{required:!0,message:"请输入人脸名称",trigger:"blur"},{min:2,max:5,message:"长度在 2 到 5 个字符",trigger:"blur"}],faceBase:[{required:!0,message:"人脸数据不能为空"}]},fakeUploadUrl:"https://jsonplaceholder.typicode.com/posts/"}},created:function(){this.getFaceList()},methods:{handleUpload:function(e){var t=this;this.faceObj.faceBase&&(this.faceObj.faceBase=""),this.convertToBase64(e.file).then((function(e){t.faceObj.faceBase=e}))},beforeUpload:function(e){var t=e.size/1024/1024<1;return t||this.$message.error("上传文件大小不能超过 1MB!"),t},handleExceed:function(e,t){this.$message.error("只能上传 1 个文件!")},handleRemove:function(){this.faceObj.faceBase=""},convertToBase64:function(e){return new Promise((function(t,a){var i=new FileReader;i.readAsDataURL(e),i.onload=function(){t(i.result)},i.onerror=function(e){a(e)}}))},submitForm:function(e){var t=this;this.$refs[e].validate((function(e){if(!e)return!1;t.$http.post("/mydb/"+(t.addAndUpdateState?"save":"update"),t.faceObj).then((function(e){t.$message({showClose:!0,message:e.data.msg,type:"success",duration:3e3}),t.getFaceList(),t.saveAndUpdateDialog=!1}))}))},resetForm:function(e){this.$refs[e].resetFields(),this.faceObj.faceBase=""},selectImg:function(e){this.selectImgDialog=!0,this.currentImg=e},getFaceList:function(){var e=this,t={current:this.current,size:this.size,faceName:this.searchFace.faceName,fid:this.faceObj.fid};this.$http.get("/mydb/faceList",{params:t}).then((function(t){e.tableData=t.data.data.records,e.total=t.data.data.total,e.size=t.data.data.size,e.current=t.data.data.current}))},handleSizeChange:function(e){this.size=e,this.getFaceList()},handleCurrentChange:function(e){this.current=e,this.getFaceList()},handleClose:function(){this.saveAndUpdateDialog=!1,this.resetForm("ruleForm")},handleSave:function(){this.addAndUpdateState=!0,this.saveAndUpdateDialog=!0,this.saveAndUpdateTitle="添加人脸",this.faceObj.faceBase=""},handleEdit:function(e,t){var a=this;this.addAndUpdateState=!1,this.saveAndUpdateDialog=!0,this.saveAndUpdateTitle="修改人脸",this.$http.get("/mydb/info/"+t.faceId).then((function(e){a.faceObj=e.data.data,a.faceObj.faceId=t.faceId}))},handleDelete:function(e,t){var a=this;this.$http.get("/mydb/faceDelete/"+t.faceId).then((function(e){a.$message({showClose:!0,message:e.data.msg,type:"success",duration:3e3}),a.getFaceList()}))}}},l=n;var r=a(1001),c=(0,r.Z)(l,i,s,!1,null,null,null);const o=c.exports}}]);
//# sourceMappingURL=996.be880868.js.map