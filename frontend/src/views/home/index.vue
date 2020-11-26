<template>
  <div>
    <el-main>
      <el-row>
        <el-col :span="5" align="left">
          <div style="font-family: 微软雅黑">MAGIC云盘 - {{ space }}</div>
        </el-col>

        <el-col :span="15" style="margin-top: -10px" align="right">
          <!-- 当前角色 -->
          <el-button
            type="text"
            icon="el-icon-user-solid"
            disabled
            size="medium"
            >{{ username }}({{ currentUserRole }})</el-button
          >
        </el-col>
        <el-col :span="2" style="margin-top: -10px">
          <!-- 登出 -->
          <el-button
            type="text"
            icon="el-icon-caret-right"
            size="medium"
            @click="logout"
            >Sign Out</el-button
          >
        </el-col>

        <el-col :span="2" style="margin-top: -10px">
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

    <el-row>
      <!-- 交互按钮 -->
      <el-col :span="2" align="right">
        <el-upload
          class="upload-demo"
          action="https://jsonplaceholder.typicode.com/posts/"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :before-remove="beforeRemove"
          multiple
          :limit="3"
          :on-exceed="handleExceed"
          :file-list="fileList"
        >
          <el-button type="primary" size="medium">上传文件</el-button>
        </el-upload>
      </el-col>
      <el-col :span="2" align="center">
        <el-button type="success" size="medium">下载文件</el-button>
      </el-col>
      <el-col :span="2" align="left">
        <el-button type="danger" size="medium">删除文件</el-button>
      </el-col>
      <el-col :span="2" align="center">
        <el-button size="medium">新建文件夹</el-button>
      </el-col>
      <el-col :span="2" align="right">
        <el-button type="danger" size="medium" plain>删除文件夹</el-button>
      </el-col>
      <el-col :span="4" align="center">
        <el-button size="medium" @click="toggleSelection()">取消选择</el-button>
      </el-col>

      <!-- 搜索入口 -->
      <el-col :span="10" align="right">
        <el-input
          placeholder="输入关键字搜索"
          v-model="searchText"
          class="input-with-select"
          clearable=""
        >
          <el-button slot="append" icon="el-icon-search"></el-button>
        </el-input>
      </el-col>
    </el-row>

    <!-- 文件/文件夹列表 -->
    <el-table
      ref="multipleTable"
      :data="tableData"
      tooltip-effect="dark"
      style="width: 95%"
      @selection-change="handleSelectionChange"
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
  deleteFile,
  updateFile,
  download,
  getFile,
} from "@/api/file";

export default {
  data() {
    return {
      username: "",
      userRoles: [],
      currentUserRole: "",
      space: "104G/138G",
      searchText: "",
      fileList: [],
      tableData: [],
      tableLoading: true,
      multipleSelection: [],
      currentFolder: "/",
      imagePrevieDialogVisible: false,
      imagePrevieDialogLoading: true,
      previewSrc: "",
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
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file);
    },
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    hitFolder(path) {
      this.tableLoading = true;
      this.currentFolder = path;
      getFiles(path).then((response) => {
        this.tableData = response.files;
        this.tableLoading = false;
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
      getFiles(last).then((response) => {
        this.tableData = response.files;
        this.tableLoading = false;
      });
    },
    previewImage(path, fileName) {
      this.previewSrc = `http://localhost:8080/api/file/download?folderName=${path}&fileName=${fileName}`;
      this.imagePrevieDialogVisible = true;
    },
    upload() {},
    downloadFiles() {},
    deleteFiles() {},
    newFolder() {},
    deleteFolder() {},
    search() {},
  },
};
</script>