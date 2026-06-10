import request from './axios'

const http ={
   /**
     * methods: 请求
     * @param url 请求地址 
     * @param params 请求参数
     */
  get(url,params){
      const config = {
          method: 'get',
          url:url
      }
      if(params) config.params = params
      return request(config)
  },
  post(url,params){
      const config = {
          method: 'post',
          url:url,
      }
      if(params) config.data = params
      return request(config)
  },

  filePost(url,params){
    const config = {
        method: 'post',
        url:url,
        headers:{
          'Content-Type': 'multipart/form-data' 
        },
        data:params
    }
    return request(config)
  }


}
export default http