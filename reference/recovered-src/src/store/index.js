import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        // （1代表 now 2代表1小时之前）
        selectBur:1,
        //传感器数据
        sensorData:{}
    },
    mutations: {
        // setter
        SET_SELECTBUR:(state,selectBur) =>{
            state.selectBur = selectBur
        },
        SET_SENSORDATA:(state,sensorData) =>{
            state.sensorData = sensorData
        },

    },
    getters: {
        selectBur: state => state.selectBur,
        sensorData: state => state.sensorData

    },
    actions: {

    }

})