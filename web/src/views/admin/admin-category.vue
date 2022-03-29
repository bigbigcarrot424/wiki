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
          <a-input v-model:value="param.name" placeholder="请输入分类名称">
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
              :data-source="categorys"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar" />
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
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
  <a-modal v-model:visible="visible" title="分类表单" @ok="handleOk" :confirm-loading="modalLoading">
    <a-form :model="category" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="名称">
        <a-input v-model:value="category.name" />
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent" />
      </a-form-item>
      <a-form-item label="顺序">
        <a-input v-model:value="category.sort" />
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
    name: 'AdminCategory',
    setup() {
      const param = ref();
      param.value = {};
      const categorys = ref();
      const pagination = ref({
        current: 1,
        pageSize: 8,
        total: 0
      });
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
          // key: 'parent',
          dataIndex: 'parent',
        },
        {
          title: '顺序',
          dataIndex: 'sort'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      const visible = ref<boolean>(false);


      // ----------- 表单 -----------
      const category = ref({});
      const showModal = () => {
        visible.value = true;
      };

      const handleOk = (e: MouseEvent) => {
        loading.value = false;

        axios.post("/category/save", category.value).then((response) => {
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
        category.value = Tool.copy(record);
      };

      /**
       *  新增
       */
      const add = (record: any) => {
        visible.value = true;
        category.value = {};
      };

      const handleDelete = (id: number) => {
        axios.delete("/category/delete/" + id).then((response) => {
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
        axios.get("/category/list", {
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
            categorys.value = data.content.list;
            //重置分页按钮
            pagination.value.current = params.page
            pagination.value.total = data.content.total
          }else {
            message.error(data.message);
          }
        })
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
        // handleQueryCategory();
        handleQuery({
          //这里要跟后端的请求名字对应起来
          page: 1,
          size: pagination.value.pageSize
        });
      });

      return {
        param,
        categorys,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        visible,
        showModal,
        handleOk,
        edit,
        category,
        add,
        handleDelete,

        /*getCategoryName,

        edit,
        add,

        category,
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
