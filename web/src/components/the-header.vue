<template>
    <a-layout-header class="header">
        <div class="logo" />
        <a-menu
                theme="dark"
                mode="horizontal"
                :style="{ lineHeight: '64px' }"
        >
            <a-menu-item key="/">
                <router-link to="/">首页</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/user">
                <router-link to="/admin/user">用户管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/ebook">
                <router-link to="/admin/ebook">电子书管理</router-link>
            </a-menu-item>
            <a-menu-item key="/admin/category">
                <router-link to="/admin/category">分类管理</router-link>
            </a-menu-item>
            <a-menu-item key="/about">
                <router-link to="/about">关于我们</router-link>
            </a-menu-item>
            <a class="login-menu" @click="showLoginModal">
                <span>登录</span>
            </a>
        </a-menu>
        <a-modal v-model:visible="loginModalVisible" title="登录" @ok="login" :confirm-loading="loginModalLoading">
            <a-form :model="loginUser" :label-col="labelCol" :wrapper-col="wrapperCol">
                <a-form-item label="登录名">
                    <a-input v-model:value="loginUser.loginName" :disabled="!!user.id"/>
                </a-form-item>
                <a-form-item label="密码">
                    <a-input v-model:value="loginUser.password" type="password"/>
                </a-form-item>
            </a-form>
        </a-modal>
    </a-layout-header>
</template>

<script lang="ts">
    import { defineComponent, ref } from 'vue';
    import axios from "axios";
    export default defineComponent({
        name: 'the-header',
        setup(){
            const loginUser = ref({
                loginName: "test",
                password: "test",
            })
            let loginModalVisible = ref(false);
            let loginModalLoading = ref(false);
            const showLoginModal = () => {
                loginModalVisible.value = true;
            }

            const login = () => {
                loginModalLoading.value = true;
                axios.post("/user/login",loginUser.value).then((response) => {
                    loginModalLoading.value = false;

                })
            }
            return {
                loginModalVisible,
                loginModalLoading,
                showLoginModal,
                loginUser,
                login,
            }
        }
    });
</script>
<style>
    .login-menu {
        float: right;
        color: white;
    }
</style>