import { createStore } from 'vuex'
declare let SessionStorage: any;
const USER = "USER";

const store = createStore({
  state: {
    //如果获取不到，就加上这个空对象，避免空指针异常
    user: SessionStorage.get(USER) || {}
  },
  getters: {
  },
  mutations: {
    setUser(state, user){
      state.user = user;
      SessionStorage.set(USER, user);
    }
  },
  actions: {
  },
  modules: {
  }
})

export default store;