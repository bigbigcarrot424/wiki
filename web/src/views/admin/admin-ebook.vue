<template>
  <a-layout>
    <a-layout-content
            :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="about">
        <h1>电子书管理</h1>
      </div>
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
</template>
