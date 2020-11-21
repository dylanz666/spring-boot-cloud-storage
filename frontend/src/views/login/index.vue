<template>
  <div align="center" class="login-container">
    <div style="margin-top: 100px">
      <h2 style="color: white">Sign in to Magic</h2>
      <el-card
        shadow="always"
        style="width: 380px; height: 290px; padding: 10px"
      >
        <el-form
          :model="ruleForm"
          status-icon
          :rules="rules"
          ref="ruleForm"
          class="demo-ruleForm"
        >
          <div align="left">
            <span>Username</span>
          </div>
          <el-form-item prop="username">
            <el-input v-model="ruleForm.username" autocomplete="off"></el-input>
          </el-form-item>

          <div align="left">
            <span>Password</span>
          </div>

          <el-form-item prop="password">
            <el-input
              type="password"
              v-model="ruleForm.password"
              autocomplete="off"
            ></el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              @click="login('ruleForm')"
              style="width: 100%"
              >Sign in</el-button
            >
          </el-form-item>
        </el-form>

        <el-row>
          <el-col :span="12" align="left"
            ><el-link href="/#/resetPassword.html" target="_blank"
              >Forgot password?</el-link
            >
          </el-col>
          <el-col :span="12" align="right">
            <el-link href="/#/createAccount.html" target="_blank"
              >Create an account.</el-link
            >
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script>
import { login } from "@/api/login";

export default {
  name: "login",
  data() {
    var validateUsername = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the username"));
      } else {
        if (this.ruleForm.checkUsername !== "") {
          this.$refs.ruleForm.validateField("password");
        }
        callback();
      }
    };
    var validatePassword = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the password"));
      } else if (value !== this.ruleForm.password) {
        callback(new Error("Two inputs don't match!"));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        username: "",
        password: "",
      },
      rules: {
        username: [{ validator: validateUsername, trigger: "blur" }],
        password: [{ validator: validatePassword, trigger: "blur" }],
      },
    };
  },
  methods: {
    login(formName) {
      const from = this.$route.query.from || "home.html";
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          return false;
        }
        login(this.ruleForm.username, this.ruleForm.password).then(
          (response) => {
            this.showSuccessNotify(response);
            this.ruleForm.username = "";
            this.ruleForm.password = "";
            if (response.code == 200 && response.status == "success") {
              this.$router.push({ path: `/${from}` });
            }
          }
        );
      });
    },
    showSuccessNotify(response) {
      if ("success" == response.status) {
        this.$notify({
          title: "Success",
          message: response.message,
          type: "success",
          offset: 100,
        });
      }
    },
  },
};
</script>

<style rel="stylesheet/scss" lang="scss">
$bg: #2d3a4b;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
  position: fixed;
  height: 100%;
  width: 100%;
  background-color: $bg;
  input {
    background: transparent;
    border: 0px;
    -webkit-appearance: none;
    border-radius: 0px;
    padding: 12px 5px 12px 15px;
    height: 47px;
  }
  .el-input {
    height: 47px;
    width: 85%;
  }
  .tips {
    font-size: 14px;
    color: #fff;
    margin-bottom: 10px;
  }
  .svg-container {
    padding: 6px 5px 6px 15px;
    color: $dark_gray;
    vertical-align: middle;
    width: 30px;
    display: inline-block;
    &_login {
      font-size: 20px;
    }
  }
  .title {
    font-size: 26px;
    font-weight: 400;
    color: $light_gray;
    margin: 0px auto 40px auto;
    text-align: center;
    font-weight: bold;
  }
  .login-form {
    position: absolute;
    left: 0;
    right: 0;
    width: 400px;
    padding: 35px 35px 15px 35px;
    margin: 120px auto;
  }
  .el-form-item {
    border: 1px solid rgba(255, 255, 255, 0.1);
    background: rgba(117, 137, 230, 0.1);
    border-radius: 5px;
    color: #454545;
  }
  .show-pwd {
    position: absolute;
    right: 10px;
    top: 7px;
    font-size: 16px;
    color: $dark_gray;
    cursor: pointer;
    user-select: none;
  }
  .thirdparty-button {
    position: absolute;
    right: 35px;
    bottom: 28px;
  }
}

.title-container {
  position: relative;
  .title {
    font-size: 26px;
    font-weight: 400;
    color: $light_gray;
    margin: 0px auto 40px auto;
    text-align: center;
    font-weight: bold;
  }
  .set-language {
    color: #fff;
    position: absolute;
    top: 5px;
    right: 0px;
  }
}
</style>
