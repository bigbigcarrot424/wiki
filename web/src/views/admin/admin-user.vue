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
          <a-input v-model:value="param.name" placeholder="登陆名">
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
              :data-source="users"
              :pagination="pagination"
              :loading="loading"
              @change="handleTableChange"
      >
        <template v-slot:category="{text, record}">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-popconfirm
                    title="是否重置密码？（原来的密码将会丢失）"
                    ok-text="是"
                    cancel-text="否"
                    @confirm="resetPassword(record)"
            >
              <a-button type="primary">
                重置密码
              </a-button>
            </a-popconfirm>

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
  <a-modal v-model:visible="visible" title="用户表单" @ok="handleOk" :confirm-loading="modalLoading">
    <a-form :model="user" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="登陆名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!user.id">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal v-model:visible="resetVisible" title="重置密码" @ok="handleResetOk" :confirm-loading="resetModalLoading">
    <a-form :model="user" :label-col="labelCol" :wrapper-col="wrapperCol">
      <a-form-item label="新密码">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref } from 'vue';
  import axios from 'axios';
  import { message } from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  declare let hexMd5: any;
  declare let KEY: any;


  export default defineComponent({
    name: 'AdminUser',
    setup() {
      const param = ref();
      param.value = {};
      const users = ref();
      const pagination = ref({
        current: 1,
        pageSize: 3,
        total: 0
      });

      const columns = [
        {
          title: '登陆名',
          dataIndex: 'loginName'
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '密码',
          dataIndex: 'password'
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      const visible = ref<boolean>(false);
      const resetVisible = ref<boolean>(false);
      const loading = ref(false);
      const resetModalLoading = ref(false);


      const user = ref();

      // ----------- 表单 -----------

      const showModal = () => {
        visible.value = true;
      };

      const handleOk = (e: MouseEvent) => {
        loading.value = false;
        user.value.password = hexMd5(user.value.password + KEY);
        axios.post("/user/save", user.value).then((response) => {
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
        user.value = Tool.copy(record);
      };



       // -------------- 重置密码 ---------------

      const showResetModal = () => {
        resetVisible.value = true;
      };

      const handleResetOk = (e: MouseEvent) => {
        resetModalLoading.value = false;
        user.value.password = hexMd5(user.value.password + KEY);
        axios.post("/user/reset-password", user.value).then((response) => {
          resetModalLoading.value = false;
          const data = response.data;
          if (data.success){
            resetVisible.value = false;
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
       * 重置密码显示
       */
      const resetPassword = (record: any) => {
        resetVisible.value = true;
        user.value = Tool.copy(record);
        user.value.password = null;
      };

      /**
       *  新增
       */
      const add = (record: any) => {
        visible.value = true;
        user.value = {};
      };

      /**
       * 删除
       */

      const handleDelete = (id: number) => {
        axios.delete("/user/delete/" + id).then((response) => {
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
        user.value = [];
        axios.get("/user/list", {
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
            users.value = data.content.list;
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
        handleQuery({page: 1, size: pagination.value.pageSize});
      })

      return {
        param,
        users,
        pagination,
        columns,
        loading,
        handleTableChange,
        handleQuery,

        visible,
        showModal,
        handleOk,
        edit,
        user,
        add,
        handleDelete,

        resetVisible,
        handleResetOk,
        resetModalLoading,
        resetPassword,
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
