<template>
  <div id="app" class="app-container" :class="{ 'high-contrast': isHighContrast, 'large-text': isLargeText }">
    <router-view v-if="isLoggedIn || $route.path === '/login' || $route.path === '/register'" />
    <router-view v-else />
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    isLoggedIn() {
      return this.$store.getters.isLoggedIn
    },
    isHighContrast() {
      return this.$store.getters.userSettings?.highContrast
    },
    isLargeText() {
      return this.$store.getters.userSettings?.textSize === 'large'
    }
  },
  mounted() {
    this.loadSettings()
  },
  methods: {
    loadSettings() {
      if (this.isLoggedIn) {
        this.$store.dispatch('loadUserSettings')
      }
    }
  }
}
</script>

<style lang="scss">
#app {
  font-family: 'Microsoft YaHei', 'PingFang SC', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-height: 100vh;
}
</style>
