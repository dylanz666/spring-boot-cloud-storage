<template>
  <el-container class="tac">
    <el-menu
      mode="vertical"
      unique-opened
      default-active="1"
      class="el-menu-vertical-demo"
      @open="handleOpen"
      @close="handleClose"
      background-color="#006699"
      text-color="#fff"
      active-text-color="#ffd04b"
      v-bind:style="menuStyle"
    >
      <el-submenu index="1">
        <template slot="title">
          <i class="el-icon-location"></i>
          <span>Navigator One</span>
        </template>
        <el-menu-item-group title="Group One">
          <el-menu-item index="1-1">item one</el-menu-item>
          <el-menu-item index="1-2">item one</el-menu-item>
        </el-menu-item-group>
        <el-menu-item-group title="Group Two">
          <el-menu-item index="1-3">item three</el-menu-item>
        </el-menu-item-group>
        <el-submenu index="1-4">
          <template slot="title">item four</template>
          <el-menu-item index="1-4-1">item one</el-menu-item>
        </el-submenu>
      </el-submenu>
      <el-menu-item index="2">
        <i class="el-icon-menu"></i>
        <span>Navigator Two</span>
      </el-menu-item>
      <el-menu-item index="3" disabled>
        <i class="el-icon-document"></i>
        <span>Navigator Three</span>
      </el-menu-item>
      <el-menu-item index="4">
        <i class="el-icon-setting"></i>
        <span>Navigator Four</span>
      </el-menu-item>
    </el-menu>

    <el-main>
      <el-row>
        <el-col :span="18" align="left">
          <el-page-header @back="goBack" title="HOME"></el-page-header>
        </el-col>

        <el-col :span="2" style="margin-top: -10px">
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

      <!-- demo form -->
      <div style="width: 45%">
        <el-form
          ref="form"
          :model="form"
          label-width="120px"
          label-position="left"
          align="left"
        >
          <el-form-item label="Activity name">
            <el-input v-model="form.name"></el-input>
          </el-form-item>
          <el-form-item label="Activity zone">
            <el-select
              v-model="form.region"
              placeholder="please select your zone"
            >
              <el-option label="Zone one" value="shanghai"></el-option>
              <el-option label="Zone two" value="beijing"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="Activity time" align="center">
            <el-col :span="11">
              <el-date-picker
                type="date"
                placeholder="Pick a date"
                v-model="form.date1"
                style="width: 100%"
              ></el-date-picker>
            </el-col>
            <el-col class="line" :span="2">-</el-col>
            <el-col :span="11">
              <el-time-picker
                placeholder="Pick a time"
                v-model="form.date2"
                style="width: 100%"
              ></el-time-picker>
            </el-col>
          </el-form-item>
          <el-form-item label="Instant delivery">
            <el-switch v-model="form.delivery"></el-switch>
          </el-form-item>
          <el-form-item label="Activity type">
            <el-checkbox-group v-model="form.type">
              <el-checkbox label="Online activities" name="type"></el-checkbox>
              <el-checkbox
                label="Promotion activities"
                name="type"
              ></el-checkbox>
              <el-checkbox label="Offline activities" name="type"></el-checkbox>
              <el-checkbox
                label="Simple brand exposure"
                name="type"
              ></el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="Resources">
            <el-radio-group v-model="form.resource">
              <el-radio label="Sponsor"></el-radio>
              <el-radio label="Venue"></el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="Activity form">
            <el-input type="textarea" v-model="form.desc"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="onSubmit">Create</el-button>
            <el-button>Cancel</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-main>
  </el-container>
</template>

<script>
import { getAuth, logout } from "@/api/auth";

export default {
  name: "home",
  data() {
    return {
      username: "",
      userRoles: [],
      currentUserRole: "",
      menuStyle: {
        height: null,
      },
      form: {
        name: "",
        region: "",
        date1: "",
        date2: "",
        delivery: false,
        type: [],
        resource: "",
        desc: "",
      },
    };
  },
  created() {
    this.menuStyle.height = document.documentElement.clientHeight + "px";
    getAuth().then((response) => {
      if (response.code == 200 && response.message == "success") {
        this.username = response.username;
        this.userRoles = response.userRoles;
        this.currentUserRole = this.userRoles[0]
          ? this.userRoles[0].substring(5, this.userRoles[0].length)
          : "";
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
    goBack() {
      console.log("back to home");
    },
    onSubmit() {
      this.$notify({
        title: "submit",
        message: "success",
        type: "success",
        offset: 100,
      });
    },
    handleOpen(key, keyPath) {
      console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      console.log(key, keyPath);
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 10px;
  .no-redirect {
    color: #97a8be;
    cursor: text;
  }

  .right-menu {
    float: right;
    height: 100%;
    &:focus {
      outline: none;
    }
    .right-menu-item {
      display: inline-block;
      margin: 0 8px;
    }
    .screenfull {
      height: 20px;
    }
    .international {
      vertical-align: top;
    }
    .theme-switch {
      vertical-align: 15px;
    }
    .avatar-container {
      height: 50px;
      margin-right: 30px;
      .avatar-wrapper {
        cursor: pointer;
        margin-top: 5px;
        position: relative;
        .user-avatar {
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }
        .el-icon-caret-bottom {
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
