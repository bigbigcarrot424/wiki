<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-form
              layout="inline"
              :model="param"
              @finish="handleFinish"
              @finishFailed="handleFinishFailed"
      >
        <a-form-item>
          <a-input v-model:value="param.name" placeholder="请输入电子书名称">
            <template #prefix><edit-two-tone/></template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
            查询
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="add()">
            新增
          </a-button>
        </a-form-item>
      </a-form>

      <a-table
              :columns="columns"
              :row-key="record => record.id"
              :data-source="ebooks"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:category="{text, record}">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <router-link to="/admin/doc">
              <a-button type="primary" @click="edit(record)">
                文档管理
              </a-button>
            </router-link>

            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>

            <a-popconfirm
                    title="删除后不可恢复，确认删除？"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal v-model:visible="visible" title="电子书详情" @ok="handleOk" :confirm-loading="modalLoading">
    <a-form :model="ebook" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类">
        <a-cascader v-model:value="categoryIds" :field-names="{label: 'name', value: 'id', children: 'children'}" :options="level1" placeholder="Please select" />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.description" type="textarea" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";

  export default defineComponent({
    name: 'AdminEbook',
    setup() {
      const param = ref();
      param.value = {};
      const ebooks = ref();
      const pagination = ref({
        current: 1,
        pageSize: 3,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '封面',
          dataIndex: 'cover',
          slots: { customRender: 'cover' }
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类',
          slots: { customRender: 'category' }
        },
        {
          title: '文档数',
          dataIndex: 'docCount'
        },
        {
          title: '阅读数',
          dataIndex: 'viewCount'
        },
        {
          title: '点赞数',
          dataIndex: 'voteCount'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      const visible = ref<boolean>(false);


      // ----------- 表单 -----------
      /**
       * 数组 [100, 101]对应：前端开发 / Vue
       */
      const ebook = ref();
      const categoryIds = ref();


      const showModal = () => {
        visible.value = true;
      };

      const handleOk = (e: MouseEvent) => {
        loading.value = false;
        ebook.value.category1Id = categoryIds.value[0];
        ebook.value.category2Id = categoryIds.value[1];
        axios.post("/ebook/save", ebook.value).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success){
            visible.value = false;
            //  重新加载列表
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          }else {
            message.error(data.message);
          }
        })
      };

      /**
       * 编辑
       */
      const edit = (record: any) => {
        visible.value = true;
        ebook.value = Tool.copy(record);
        categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
      };

      /**
       *  新增
       */
      const add = (record: any) => {
        visible.value = true;
        ebook.value = {};
      };

      const handleDelete = (id: number) => {
        axios.delete("/ebook/delete/" + id).then((response) => {
          const data = response.data;
          if(data.success) {
            handleQuery({
              page: pagination.value.current,
              size: pagination.value.pageSize,
            });
          }
        });
      };

      /**
       * 数据查询
       **/
      //这个params参数可以起任意的名字
      const handleQuery = (params: any) => {
        loading.value = true;
        // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑，则列表显示的还是编辑前的数据
        ebook.value = [];
        axios.get("/ebook/list", {
          // 这里可以用params: params ，前面是get请求的参数，后面是传入的参数名字，因为一般不会将参数都用到，所以可以分开传进去
          params:{
            page: params.page,
            size: params.size,
            name: param.value.name
          }
        }).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success){
            ebooks.value = data.content.list;
            //重置分页按钮
            pagination.value.current = params.page
            pagination.value.total = data.content.total
          }else {
            message.error(data.message);
          }
        })
      };

      const level1 = ref();
      let categorys: any;
      /**
       * 查询所有分类
       */

      const handleQueryCategory = () => {
        loading.value = true;
        axios.get("/category/all").then((response) => {
          loading.value = false;
          const data = response.data;
          if(data.success) {
            categorys = data.content;
            console.log("原始数组:", categorys);

            level1.value = [];
            level1.value = Tool.array2Tree(categorys, 0);
            console.log("树形结构:", level1.value);
            //加载完分类后，再加载电子书，否则如果分类树加载的很慢，则电子书渲染会报错
            handleQuery({
              //这里要跟后端的请求名字对应起来
              page: 1,
              size: pagination.value.pageSize
            });
          }else {
            message.error(data.message);
          }
        })
      }

      const getCategoryName = (cid: number) => {
        let result = "";
        if(categorys){
          categorys.forEach((item: any) => {
            if(item.id === cid){
              result = item.name;
            }
          });
        }
        return result;
      };

      /**
       * 表格点击页码时触发
       */
      const handleTableChange = (pagination: any) => {
        console.log("看看自带的分页参数都有啥：" + pagination);
        handleQuery({
          page: pagination.current,
          size: pagination.pageSize
        });
      };

      onMounted(() => {
        handleQueryCategory();

      });

      return {
        param,
        ebooks,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        visible,
        showModal,
        handleOk,
        edit,
        ebook,
        add,
        handleDelete,
        categoryIds,
        level1,

        getCategoryName,

        /*getCategoryName,

        edit,
        add,

        ebook,
        modalVisible,
        modalLoading,
        handleModalOk,
        categoryIds,
        level1,

        handleDelete*/
      }
    }
  });
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }
</style>
