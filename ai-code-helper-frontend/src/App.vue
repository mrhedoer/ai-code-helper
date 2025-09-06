<template>
  <div id="app">
    <ChatRoom />
  </div>
</template>

<script>
import ChatRoom from './components/ChatRoom.vue'

export default {
  name: 'App',
  components: {
    ChatRoom
  }
}
</script>

<style>
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>
