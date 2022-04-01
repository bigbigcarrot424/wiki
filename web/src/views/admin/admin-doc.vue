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
                    @confirm="showConfirm(record)"
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
  <a-modal v-model:visible="visible" title="文档表单" @ok="handleOk" :confirm-loading="modalLoading">
    <a-form :model="doc" :label-col="labelCol" :wrapper-col="wrapperCol">

      <a-form-item label="名称">
        <a-input v-model:value="doc.name" />
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
                v-model:value="doc.parent"
                show-search
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                placeholder="请选择父文档"
                allow-clear
                tree-default-expand-all
                :tree-data="treeSelectData"
                :replaceFields="{title: 'name', key: 'id', value: 'id'}"
        >
        </a-tree-select>
      </a-form-item>

      <a-form-item label="顺序">
        <a-input v-model:value="doc.sort" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import {message, Modal} from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import {ExclamationCircleOutlined} from "@ant-design/icons-vue/lib";

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      const route = useRoute();
      const param = ref();
      param.value = {};
      const docs = ref();
      const loading = ref(false);

      const columns = [
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父文档',
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
      const treeSelectData = ref();
      treeSelectData.value = [];
      const doc = ref({});
      const showModal = () => {
        visible.value = true;
      };

      const handleOk = (e: MouseEvent) => {
        loading.value = false;

        axios.post("/doc/save", doc.value).then((response) => {
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
        doc.value = Tool.copy(record);

        //不能选择当前节点及其所有子孙节点，作为父节点，会使树断开
        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        //为树添加一个“无”
        treeSelectData.value.unshift({id: 0, name: '无'});
      };

      /**
       *  新增
       */
      const add = (record: any) => {
        visible.value = true;
        doc.value = {ebookId: route.query.ebookId};

        treeSelectData.value = Tool.copy(level1.value);
        //为树添加一个“无”
        treeSelectData.value.unshift({id: 0, name: '无'});
      };

      /**
       * 删除
       */
      const handleDelete = (id: number) => {
        getDeleteIds(level1.value, id);
        axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
          const data = response.data;
          if(data.success) {
            handleQuery();
          }
        });
      };

      /**
       * 弹出二次确认框
       */
      const showConfirm = (record: any) => {
        names.splice(0,names.length);
        getDeleteNames(level1.value, record.id);
        console.log(names);
        Modal.confirm({
          title: '确定要删除这些文档,包括[' + names + ']?',
          icon: createVNode(ExclamationCircleOutlined),
          content: createVNode('div', { style: 'color:red;' }, '该文档与其子文档都会被删除'),
          onOk() {
            handleDelete(record.id)
          },
          onCancel() {
            console.log('Cancel');
          },
          class: 'test',
        });
      };

      /**
       * 将某节点及其子孙节点全部置为disabled
       */
      const setDisable = (treeSelectData: any, id: any) => {
        for(let i = 0; i < treeSelectData.length; i ++){
          const node = treeSelectData[i];
          if(node.id === id) {
            console.log("disabled", node);
            node.disabled = true;

            const children = node.children;
            if (Tool.isNotEmpty(children)){
              for(let j = 0; j < children.length; j ++){
                setDisable(children, children[j].id);
              }
            }
          } else {
            const children = node.children;
            if (Tool.isNotEmpty(children)){
              setDisable(children, id);
            }
          }
        }
      };

      const ids: Array<string> = [];

      /**
       * 查找整根树枝
       */
      const getDeleteIds = (treeSelectData: any, id: any) => {
        for(let i = 0; i < treeSelectData.length; i ++){
          const node = treeSelectData[i];
          if(node.id === id) {
            //将目标id放入结果集ids
            ids.push(id);

            const children = node.children;
            if (Tool.isNotEmpty(children)){
              for(let j = 0; j < children.length; j ++){
                getDeleteIds(children, children[j].id);
              }
            }
          } else {
            const children = node.children;
            if (Tool.isNotEmpty(children)){
              getDeleteIds(children, id);
            }
          }
        }
      };
      /**
       * 查找待删除的名称
       */

      const names: Array<string> = [];
      const getDeleteNames = (treeSelectData: any, id: any) => {
        // 清空数组，清空上一次的查询，因为这是个递归函数，会自己调用自己，因此不能在函数入口简单的写一个清空

        for(let i = 0; i < treeSelectData.length; i ++){
          const node = treeSelectData[i];
          if(node.id === id) {
            //将目标name放入结果集names
            names.push(node.name);

            const children = node.children;
            if (Tool.isNotEmpty(children)){
              for(let j = 0; j < children.length; j ++){
                getDeleteNames(children, children[j].id);
              }
            }
          } else {
            const children = node.children;
            if (Tool.isNotEmpty(children)){
              getDeleteNames(children, id);
            }
          }
        }
      };

      const level1 = ref();

      /**
       * 数据查询
       **/
      //这个params参数可以起任意的名字
      const handleQuery = () => {
        loading.value = true;
        axios.get("/doc/all").then((response) => {
          const data = response.data;
          loading.value = false;
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
        // handleQueryDoc();
        handleQuery();
      });

      return {
        param,
        level1,
        // docs,
        columns,
        loading,
        handleQuery,

        visible,
        showModal,
        handleOk,
        edit,
        doc,
        add,
        handleDelete,

        treeSelectData,
        showConfirm,

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
