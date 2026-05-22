<script>
  export default {
    onLaunch() {
      const token = uni.getStorageSync('token')
      if (!token) {
        uni.reLaunch({ url: '/pages/index/index' })
      }
    }
  }
</script>
<style>
  page { background-color: #FAFAF9; font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif; }
</style>
