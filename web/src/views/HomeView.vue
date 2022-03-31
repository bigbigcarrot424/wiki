<template>
  <div>
    <a-layout>
      <a-layout-sider width="200" style="background: #fff">
        <a-menu
                mode="inline"
                :style="{ height: '100%', borderRight: 0 }"
                @click="handleClick"
        >
          <a-menu-item key="welcome">
            <router-link :to="'/'">
              <MailOutlined/>
              <span>欢迎</span>
            </router-link>
          </a-menu-item>
          <a-sub-menu v-for="item in level1" :key="item.id">
            <template v-slot:title>
                <span>
                  <user-outlined />
                  {{item.name}}
                </span>
            </template>

            <a-menu-item v-for="child in item.children" :key="child.id">
              <MailOutlined/><span>{{child.name}}</span>
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-layout-sider>
      <a-layout-content
              :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <a-list item-layout="vertical" size="large" :data-source="ebooks" :grid="{ gutter: 16, column: 3 }">
          <template #renderItem="{ item }">
            <a-list-item key="item.name">
              <template #actions>
                <span v-for="{ type, text } in actions" :key="type">
                  <component :is="type" style="margin-right: 8px" />
                  {{ text }}
                </span>
              </template>
              <a-list-item-meta :description="item.description">
                <template #title>
                  <a :href="item.href">{{ item.name }}</a>
                </template>
                <template #avatar><a-avatar :src="item.cover" /></template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>

      </a-layout-content>
    </a-layout>
  </div>
</template>

<script lang="ts">

import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import { defineComponent, onMounted, reactive, ref, toRef } from 'vue';
import { message } from 'ant-design-vue';
import {Tool} from "@/util/tool";
import axios from 'axios'

const listData: Record<string, string>[] = [];


export default defineComponent({
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  name: 'HomeView',
  setup(){

    const level1 = ref();
    let categorys: any;
    /**
     * 查询所有分类
     */

    const handleQueryCategory = () => {
      axios.get("/category/all").then((response) => {
        const data = response.data;
        if(data.success) {
          categorys = data.content;
          console.log("原始数组:", categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log("树形结构:", level1.value);
        }else {
          message.error(data.message);
        }
      })
    }


    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];

    const ebooks = ref();
    const ebooks1 = reactive({books:[]})

    const handleClick = () => {
      console.log("menu click");
    }

    onMounted(()=>{
      handleQueryCategory();
      axios.get('/ebook/list', {
        params:{
          page: 1,
          size: 1000,
        }
      }).then((response)=>{
        ebooks.value = response.data.content.list;
        // ebooks1.books = response.data.content.list;
        // console.log(response.data)
      })

    })
    return{
      listData,
      pagination,
      actions,
      ebooks,
      books:toRef(ebooks1, 'books'),
      handleQueryCategory,
      handleClick,
      level1,
    }
  }
});
</script>

<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }
</style>