<template>
  <div>
    <el-main>
      <el-row>
        <el-col :span="9" style="margin-top: -10px" align="right">
          <!-- 当前角色 -->
          <el-button
            type="text"
            icon="el-icon-user-solid"
            size="medium"
            @click="showSpaceInfo"
            >{{ username }}({{ currentUserRole }})</el-button
          >
        </el-col>
        <el-col :span="6" style="margin-top: -10px">
          <!-- 登出 -->
          <el-button
            type="text"
            icon="el-icon-caret-right"
            size="medium"
            @click="logout"
            >Sign Out</el-button
          >
        </el-col>

        <el-col :span="9" style="margin-top: -10px">
          <!-- 角色下拉框 -->
          <el-dropdown @command="changeRole">
            <span class="el-dropdown-link">
              <el-button type="text" size="medium" icon="el-icon-s-tools"
                >User Role</el-button
              >
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <div v-for="(item, index) in userRoles" :key="item">
                <el-dropdown-item
                  icon="el-icon-user"
                  size="medium"
                  :command="userRoles[index]"
                  >{{ userRoles[index] }}</el-dropdown-item
                >
              </div>
            </el-dropdown-menu>
          </el-dropdown>
        </el-col>
      </el-row>
      <hr />
    </el-main>

    <!-- 交互按钮 -->
    <el-row>
      <el-col :span="6">
        <el-upload
          class="upload-demo"
          ref="upload"
          action="/api/file"
          :data="uploadParams"
          :on-success="refreshTable"
          :file-list="fileList"
          :auto-upload="true"
          multiple
        >
          <el-button
            slot="trigger"
            size="medium"
            type="primary"
            @click="clearUploadFileList"
            >上传文件</el-button
          >
        </el-upload>
      </el-col>
      <el-col :span="6">
        <el-button type="success" size="medium" @click="downloadFile"
          >下载文件</el-button
        >
      </el-col>
      <el-col :span="6">
        <el-button type="danger" size="medium" @click="deleteFiles"
          >删除文件</el-button
        >
      </el-col>
      <el-col :span="6">
        <el-button type="primary" size="medium" @click="renameFile"
          >重命名</el-button
        >
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="7">
        <el-button size="medium" @click="createFolderDialogVisible = true"
          >新建文件夹</el-button
        >
      </el-col>
      <el-col :span="7">
        <el-button
          type="danger"
          size="medium"
          plain
          @click="openDeleteFolderDialog"
          >删除文件夹</el-button
        >
      </el-col>
      <el-col :span="6">
        <el-button size="medium" @click="toggleSelection()">取消选择</el-button>
      </el-col>
      <el-col :span="4"> </el-col>
    </el-row>

    <!-- 搜索入口 -->
    <el-input
      placeholder="输入关键字搜索"
      v-model="searchText"
      class="input-with-select"
      clearable=""
    >
      <el-button slot="append" icon="el-icon-search"></el-button>
    </el-input>

    <!-- 文件/文件夹列表 -->
    <el-table
      ref="multipleTable"
      :data="tableData"
      tooltip-effect="dark"
      style="width: 95%"
      height="300"
      @selection-change="selectContent"
      v-loading="tableLoading"
    >
      <el-table-column type="selection" width="55" :selectable="isSelectable">
      </el-table-column>
      <el-table-column label="内容" width="800">
        <template slot-scope="scope">
          <el-link
            v-if="scope.row.type == 'folder'"
            icon="el-icon-folder"
            @click="hitFolder(scope.row.path)"
            >{{ scope.row.name }}</el-link
          >
          <el-link
            v-if="scope.row.type == 'file'"
            icon="el-icon-document"
            href="https://element.eleme.io"
            target="_blank"
            >{{ scope.row.name }}</el-link
          >
          <el-link
            v-if="scope.row.type == 'image'"
            icon="el-icon-picture"
            @click="previewImage(scope.row.path, scope.row.name)"
            target="_blank"
            >{{ scope.row.name }}</el-link
          >
          <el-link
            v-if="scope.row.type == 'video'"
            icon="el-icon-video-play"
            href="https://element.eleme.io"
            target="_blank"
            >{{ scope.row.name }}</el-link
          >
          <el-link
            v-if="scope.row.type == 'audio'"
            icon="el-icon-headset"
            href="https://element.eleme.io"
            target="_blank"
            >{{ scope.row.name }}</el-link
          >
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小" width="200"> </el-table-column>
      <el-table-column prop="date" label="修改日期" show-overflow-tooltip>
      </el-table-column> </el-table
    ><br />

    <!-- 图片预览 -->
    <el-dialog
      title="图片预览"
      :visible.sync="imagePrevieDialogVisible"
      v-loading="imagePrevieDialogLoading"
      width="50%"
      center
    >
      <span slot="footer" class="dialog-footer">
        <div class="block">
          <el-image
            :src="previewSrc"
            @load="imagePrevieDialogLoading = false"
          ></el-image>
        </div>
      </span>
    </el-dialog>

    <el-button-group>
      <el-button type="primary" icon="el-icon-arrow-left" @click="lastFolder"
        >上一级</el-button
      >
      <el-button type="primary" icon="el-icon-arrow-share"
        >当前目录:{{ currentFolder }}</el-button
      >
    </el-button-group>
    <el-button type="success" @click="refresh">刷新页面</el-button>

    <!-- 新建文件夹 -->
    <el-dialog
      title="新建文件夹"
      :visible.sync="createFolderDialogVisible"
      width="30%"
      center
    >
      <el-input placeholder="请输入文件夹名" v-model="newFolderName" clearable>
      </el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="createFolderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="createFolder">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 删除文件夹 -->
    <el-dialog
      title="删除文件夹"
      :visible.sync="deleteFolderDialogVisible"
      width="30%"
      center
    >
      <el-select v-model="targetDeleteFolder" placeholder="请选择文件夹">
        <el-option
          v-for="item in targetDeleteFolders"
          :key="item.name"
          :label="item.name"
          :value="item.name"
        >
        </el-option>
      </el-select>
      <span slot="footer" class="dialog-footer">
        <el-button @click="deleteFolderDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="deleteFolder">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getAuth, logout } from "@/api/auth";
import {
  createFolder,
  deleteFolder,
  updateFolder,
  getFolder,
  getFiles,
} from "@/api/folder";
import {
  upload,
  uploadFiles,
  deleteFiles,
  updateFile,
  download,
  getFile,
} from "@/api/file";
import { getSpaceInfo } from "@/api/space";
let config = require("../../../config/index");

export default {
  data() {
    return {
      username: "",
      userRoles: [],
      currentUserRole: "",
      space: "",
      searchText: "",
      fileList: [],
      tableData: [],
      tableLoading: true,
      selectedContent: [],
      currentFolder: "/",
      imagePrevieDialogVisible: false,
      imagePrevieDialogLoading: true,
      previewSrc: "",
      newFolderName: "",
      createFolderDialogVisible: false,
      newFileName: "",
      renameFileDialogVisible: false,
      deleteFolderDialogVisible: false,
      targetDeleteFolders: [],
      targetDeleteFolder: "",
      uploadParams: {},
    };
  },
  created() {
    getAuth().then((response) => {
      if (response.code == 200 && response.message == "success") {
        this.username = response.username;
        this.userRoles = response.userRoles;
        this.currentUserRole = this.userRoles[0]
          ? this.userRoles[0].substring(5, this.userRoles[0].length)
          : "";

        getFiles("/").then((response) => {
          this.tableData = response.files;
          this.tableLoading = false;
          this.targetDeleteFolders = response.files.filter((item) => {
            return item.type == "folder";
          });
        });
        this.uploadParams = {
          folderName: "/",
        };
        getSpaceInfo().then(response=>{
          this.space=response.message;
        });
      }
    });
  },
  methods: {
    logout() {
      logout().then((response) => {
        if (response.code == 200 && response.status == "success") {
          window.location.href = "/#/login.html";
        }
      });
    },
    changeRole(role) {
      this.currentUserRole = role
        ? role.substring(5, this.userRoles[0].length)
        : "";
    },
    refresh() {
      window.location.reload();
    },
    refreshTable() {
      this.tableLoading = true;
      getFiles(this.currentFolder).then((response) => {
        this.tableData = response.files;
        this.tableLoading = false;
        this.targetDeleteFolders = response.files.filter((item) => {
          return item.type == "folder";
        });
      });
    },
    isSelectable(row, index) {
      if (row.type == "folder") {
        return false;
      }
      return true;
    },
    toggleSelection(rows) {
      if (rows) {
        rows.forEach((row) => {
          this.$refs.multipleTable.toggleRowSelection(row);
        });
      } else {
        this.$refs.multipleTable.clearSelection();
      }
    },
    selectContent(content) {
      this.selectedContent = content;
    },
    hitFolder(path) {
      this.tableLoading = true;
      this.currentFolder = path;
      this.uploadParams = {
        folderName: this.currentFolder,
      };
      getFiles(path).then((response) => {
        this.tableData = response.files;
        this.tableLoading = false;
        this.targetDeleteFolders = response.files.filter((item) => {
          return item.type == "folder";
        });
      });
    },
    lastFolder() {
      if (this.currentFolder == "/") {
        this.$message({
          message: "当前已在根目录",
          type: "warning",
        });
      }
      let folders = this.currentFolder.split("/");
      let last = folders.splice(0, folders.length - 1);
      last = last == "" ? "/" : last.join("/");

      this.tableLoading = true;
      this.currentFolder = last;
      this.uploadParams = {
        folderName: this.currentFolder,
      };
      getFiles(last).then((response) => {
        this.tableData = response.files;
        this.tableLoading = false;
        this.targetDeleteFolders = response.files.filter((item) => {
          return item.type == "folder";
        });
      });
    },
    previewImage(path, fileName) {
      this.previewSrc = `http://${config.host}:8080/api/file/download?folderName=${path}&fileName=${fileName}`;
      this.imagePrevieDialogVisible = true;
    },
    clearUploadFileList() {
      this.$refs.upload.clearFiles();
    },
    downloadFile() {
      if (this.selectedContent.length == 0) {
        this.$message({
          message: "请选择要下载的文件",
          type: "info",
        });
        return;
      }
      if (this.selectedContent.length > 1) {
        this.$message({
          message: "对不起，一次仅支持下载一份文件",
          type: "info",
        });
        return;
      }
      let folderName = this.selectedContent[0].path;
      let fileName = this.selectedContent[0].name;
      getFile(folderName, fileName).then((response) => {
        if (response.status == "SUCCESS") {
          window.location.href = `http://${config.host}:8080/api/file/download?folderName=${folderName}&fileName=${fileName}`;
          return;
        }
        this.$message({
          message: "对不起，文件不存在",
          type: "info",
        });
      });
    },
    deleteFiles() {
      if (this.selectedContent.length == 0) {
        this.$message({
          message: "请选择要删除的文件",
          type: "info",
        });
        return;
      }
      this.$confirm(
        `此操作将永久删除${this.selectedContent.length}份文件, 是否继续?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
      )
        .then(() => {
          this.$message({
            message: this.currentFolder,
            type: "success",
          });
          deleteFiles(this.selectedContent).then((response) => {
            if (response.message == "SUCCESS") {
              this.$message({
                message: response.message,
                type: "success",
              });
            } else {
              this.$message({
                message: response.message,
                type: "error",
              });
            }
            this.tableLoading = true;
            getFiles(this.currentFolder).then((response) => {
              this.tableData = response.files;
              this.tableLoading = false;
              this.targetDeleteFolders = response.files.filter((item) => {
                return item.type == "folder";
              });
            });
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    createFolder() {
      this.createFolderDialogVisible = false;
      let fullPath =
        this.currentFolder == "/"
          ? this.newFolderName
          : this.currentFolder + "/" + this.newFolderName;
      createFolder(fullPath).then((response) => {
        if (response.message == "SUCCESS") {
          this.$message({
            message: `文件夹:${fullPath}已创建`,
            type: "success",
          });
          this.tableLoading = true;
          getFiles(this.currentFolder).then((response) => {
            this.tableData = response.files;
            this.tableLoading = false;
            this.targetDeleteFolders = response.files.filter((item) => {
              return item.type == "folder";
            });
          });
          return;
        }
        this.$message({
          message: `文件夹:${fullPath}创建失败`,
          type: "error",
        });
      });
    },
    openDeleteFolderDialog() {
      if (this.targetDeleteFolders.length == 0) {
        this.$message({
          message: "当前无文件夹",
          type: "info",
        });
        return;
      }
      this.deleteFolderDialogVisible = true;
    },
    deleteFolder() {
      let fullPath =
        this.currentFolder == "/"
          ? this.targetDeleteFolder
          : this.currentFolder + "/" + this.targetDeleteFolder;
      let deleteFilesInTargetForder = true;

      this.$confirm("此操作将永久删除该文件夹, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          deleteFolder(fullPath, deleteFilesInTargetForder).then((response) => {
            if (response.message == "SUCCESS") {
              this.$message({
                message: `文件夹:${this.targetDeleteFolder}已删除`,
                type: "success",
              });
            } else {
              this.$message({
                message: `文件夹:${this.targetDeleteFolder}删除失败`,
                type: "error",
              });
            }

            this.targetDeleteFolder = "";
            this.deleteFolderDialogVisible = false;
            this.tableLoading = true;
            getFiles(this.currentFolder).then((response) => {
              this.tableData = response.files;
              this.tableLoading = false;
              this.targetDeleteFolders = response.files.filter((item) => {
                return item.type == "folder";
              });
            });
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    renameFile() {
      if (this.selectedContent.length == 0) {
        this.$message({
          message: "请选择要重命名的文件",
          type: "info",
        });
        return;
      }
      if (this.selectedContent.length > 1) {
        this.$message({
          message: "对不起，一次只能重命名一个文件",
          type: "warn",
        });
        return;
      }

      let type = "info";
      let errorMessage = "已取消重命名";
      this.renameFileDialogVisible = true;
      this.$prompt(`重命名无法恢复, 是否继续?`, "提示", {
        inputPlaceholder: "请输入文件名",
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      })
        .then(({ value }) => {
          if (value.indexOf(".") < 0) {
            errorMessage = "请输入文件后缀";
            type = "error";
            throw "";
          }
          let folderName = this.currentFolder;
          let currentFileName = this.selectedContent[0].name;
          let targetFileName = value;
          updateFile(folderName, currentFileName, targetFileName).then(
            (response) => {
              if (response.message == "SUCCESS") {
                this.$message({
                  message: response.message,
                  type: "success",
                });
              } else {
                this.$message({
                  message: response.message,
                  type: "error",
                });
              }
              this.renameFileDialogVisible = false;
              this.tableLoading = true;
              getFiles(this.currentFolder).then((response) => {
                this.tableData = response.files;
                this.tableLoading = false;
                this.targetDeleteFolders = response.files.filter((item) => {
                  return item.type == "folder";
                });
              });
            }
          );
        })
        .catch(() => {
          this.$message({
            type: type,
            message: errorMessage,
          });
        });
    },
    showSpaceInfo() {
      this.$alert(this.space, "MAGIC容量信息", {
        center: true,
      });
    },
    search() {},
  },
};
</script>