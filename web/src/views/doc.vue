<template>
    <a-layout>
        <a-layout-content :style="{background: '#fff', padding: '24px', margin: 0, minHeight: '280px'}">
            <a-row>
                <a-col :span="6">
                    <a-tree
                        v-if="level1&&level1.length > 0"
                        :tree-data="level1"
                        @select="onSelect"
                        :replaceFields="{title: 'name', key: 'id', value: 'id'}"
                        :defaultExpandAllRows="true"
                    >
                    </a-tree>
                </a-col>
                <a-col :span="18">

                </a-col>
            </a-row>
            <div class="div">
                <h1>欢迎来到文档页面</h1>
            </div>
        </a-layout-content>
    </a-layout>
</template>

<script lang="ts">
    import { defineComponent, onMounted, ref, createVNode } from 'vue';
    import axios from 'axios';
    import {message, Modal} from 'ant-design-vue';
    import {Tool} from "@/util/tool";
    import {useRoute} from "vue-router";
    import {ExclamationCircleOutlined} from "@ant-design/icons-vue/lib";
    import E from 'wangeditor'


    export default defineComponent({
        name: 'Doc',
        setup() {
            const route = useRoute();
            const docs = ref();
            const level1 = ref();
            level1.value = [];


            /**
             * 数据查询
             **/
            const handleQuery = () => {
                    axios.get("/doc/all/" + route.query.ebookId).then((response) => {
                        const data = response.data;
                        if (data.success){
                            docs.value = data.content;
                            console.log("原始数据", docs.value);
                            level1.value = [];
                            level1.value = Tool.array2Tree(docs.value, 0);
                            console.log("树形结构", level1.value);
                        }else {
                            message.error(data.message);
                        }
                    })
                };

            onMounted(() => {
                handleQuery();
            });

            return {
                handleQuery,
                level1,
            }
        }
    });
</script>