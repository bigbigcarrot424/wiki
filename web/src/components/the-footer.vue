<template>
    <a-layout-footer :style="{textAlign: 'center'}">
        Ant Design ©2018 Created by Fang Shuo<span v-show="user.id">欢迎，{{user.name}}</span>
    </a-layout-footer>

<!--    <a-layout-footer :style="{textAlign: 'center', position: 'fixed', zIndex: 100, width: '100%',  margin: '870px 24px 16px 0'}">-->
<!--        Ant Design ©2018 Created by Fang Shuo-->
<!--    </a-layout-footer>-->
</template>

<script lang="ts">
    import { defineComponent, computed } from 'vue';
    import store from "@/store";
    export default defineComponent({
        name: 'the-footer',
        setup(){
            const user = computed(() => store.state.user);
            return {
                user,
            }
        }
    });
</script>