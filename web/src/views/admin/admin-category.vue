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
          <a-button type="primary" @click="handleQuery()">
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
              :data-source="level1"
              :loading="loading"
              :pagination="false"
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
        <a-select
                ref="select"
                v-model:value="category.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
            {{c.name}}
          </a-select-option>
        </a-select>
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
            handleQuery();
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
            handleQuery();
          }
        });
      };

      const level1 = ref();

      /**
       * 数据查询
       **/
      //这个params参数可以起任意的名字
      const handleQuery = () => {
        loading.value = true;
        axios.get("/category/all").then((response) => {
          const data = response.data;
          loading.value = false;
          if (data.success){
            categorys.value = data.content;
            console.log("原始数据", categorys.value);
            level1.value = [];
            level1.value = Tool.array2Tree(categorys.value, 0);
            console.log("树形结构", level1.value);
          }else {
            message.error(data.message);
          }
        })
      };

      onMounted(() => {
        // handleQueryCategory();
        handleQuery();
      });

      return {
        param,
        level1,
        // categorys,
        columns,
        loading,
        handleQuery,

        visible,
        showModal,
        handleOk,
        edit,
        category,
        add,
        handleDelete,

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
