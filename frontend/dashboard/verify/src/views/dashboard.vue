<script>
import roadMap from "@/components/road-map.js"
import layer from "@/components/layer.js"
import event from "@/components/event.js"
import roadInfo from "@/components/road-info.js"
import alarm from "@/components/alarm.js"
import rcsService from "@/components/rcs-service.js"
import layerColor from "@/components/layer-color.js"
import { mapState } from "vuex"
import http from "@/utils/http"


export default ({
  name: 'dashboard',
  render: function render(){var _vm=this,_c=_vm._self._c;return _c('div',{staticClass:"wrapper"},[_c('div',{staticClass:"coloured-ribbon"}),_c('div',{staticClass:"content"},[_c('div',{staticClass:"back",on:{"click":_vm.backHome}},[_c('i',{staticClass:"fa-solid fa-reply"}),_c('span',[_vm._v("返回")])]),_vm._m(0),_c('div',{staticClass:"left"},[_c('div',{staticClass:"left-panel"},[_vm._v("S "),_c('div',{staticClass:"panel-btn dis ani",on:{"click":_vm.panelBtn}},[_vm._v("实时数据")])]),_c('div',{attrs:{"id":"layer-left"}},[_c('layer',{directives:[{name:"append-to",rawName:"v-append-to",value:(_vm.layerPos),expression:"layerPos"}],on:{"changeNormalLayerType":_vm.changeNormalLayerType,"changeRoadNetLayer":_vm.changeRoadNetLayer,"changeMeteorologicalType":_vm.changeMeteorologicalType,"changeVehicleEventType":_vm.changeVehicleEventType}})],1)]),_c('div',{staticClass:"center"},[_c('road-map',{ref:"roadMap",attrs:{"normalLayerType":_vm.normalLayerType,"roadNetLayerItem":_vm.roadNetLayerItem,"slipperyPoint":_vm.slipperyPoint,"lowAttachmentPoint":_vm.lowAttachmentPoint,"meteorologicalType":_vm.meteorologicalType,"rainPointData":_vm.rainPointData,"vehicleEventType":_vm.vehicleEventType},on:{"setRoadCondition":_vm.setRoadCondition,"deleteEvent":_vm.deleteEvent,"mapClickSensor":_vm.mapClickSensor}}),_c('layer-color',{directives:[{name:"show",rawName:"v-show",value:(_vm.isShowColor),expression:"isShowColor"}],attrs:{"roadNetLayerType":_vm.roadNetLayerType}}),_c('div',{directives:[{name:"show",rawName:"v-show",value:(this.isShowOutEvent && this.vehicleEventType),expression:"this.isShowOutEvent && this.vehicleEventType"}],staticClass:"outside_event_div"},[_c('div',{staticClass:"event_data_div",class:_vm.roadsideEventColor},[_c('span',[_vm._v("车牌号码：")]),_c('span',[_vm._v(_vm._s(_vm.outEventData.plateNum))]),_vm._v(" "),_c('span',[_vm._v("已经开出锡山区")]),_vm._v("  "),_c('span',[_vm._v("上报事件："+_vm._s(_vm.outEventData.outsideEvent))]),_vm._v("  "),_c('span',[_vm._v("上报位置："+_vm._s(_vm.outEventData.coordinates))]),_vm._v("   "),_c('span',[_vm._v("上报时间：")]),_c('span',[_vm._v(_vm._s(_vm.outEventData.outsideEventTime))]),_vm._v("  ")])])],1),_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.is_show_right),expression:"is_show_right"}],staticClass:"right"},[_c('div',{staticClass:"right-l",attrs:{"id":"road-info-and-alarm"}},[_c('road-info',{directives:[{name:"append-to",rawName:"v-append-to",value:(_vm.roadInfoAndAlarmPos),expression:"roadInfoAndAlarmPos"}],attrs:{"chartData":_vm.chartData,"segmentInfo":_vm.segmentInfo,"sensorData":_vm.sensorData},on:{"changeType":_vm.changeType,"changesensor":_vm.changeSensor}}),_c('alarm',{directives:[{name:"append-to",rawName:"v-append-to",value:(_vm.roadInfoAndAlarmPos),expression:"roadInfoAndAlarmPos"}],attrs:{"alarmList":_vm.alarmList}})],1),_c('div',{staticClass:"right-r",attrs:{"id":"rcs-event"}},[_c('rcs-service',{directives:[{name:"append-to",rawName:"v-append-to",value:(_vm.rcsAndEventPos),expression:"rcsAndEventPos"}],attrs:{"rcsData":_vm.rcsData,"summaryEventData":_vm.summaryEventData}})],1)]),_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.drawer),expression:"drawer"}],staticClass:"section panel-in-ani"},[_c('div',{staticClass:"close-btn",on:{"click":_vm.closeBtn}},[_vm._v("×")]),_c('div',{staticClass:"drawer-left",attrs:{"id":"layer-drawer"}}),_c('div',{staticClass:"drawer-center",attrs:{"id":"road-info-and-alarm-drawer"}}),_c('div',{staticClass:"drawer-right",attrs:{"id":"rcs-event-drawer"}})]),_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.slideIsShow),expression:"slideIsShow"}],ref:"slideshow",staticClass:"slideshow"},[_c('el-carousel',{staticClass:"customSlider",attrs:{"indicator-position":"outside","height":_vm.adaptationHighly}},_vm._l((_vm.sliderImgArray),function(item){return _c('el-carousel-item',{key:item.id},[_c('img',{attrs:{"src":item.url}})])}),1),_c('div',{staticClass:"slideShowBtn",on:{"click":_vm.closeSlide}},[_vm._v("×")])],1)])])
},
  staticRenderFns: [function (){var _vm=this,_c=_vm._self._c;return _c('div',{staticClass:"top"},[_c('div',{staticClass:"top-pannel"},[_vm._v(" 道路检测与预警平台 ")])])
}],
  components: {
    roadMap: roadMap,
    layer: layer,
    event: event,
    roadInfo: roadInfo,
    alarm: alarm,
    rcsService: rcsService,
    layerColor: layerColor
  },
  directives: {
    appendTo: null
  },

  data() {
    return {
      sliderImgArray: [{
        id: 0,
        url: ""
      }, {
        id: 1,
        url: ""
      }],
      slideIsShow: false,
      //是否显示轮播图
      normalLayerType: [],
      roadNetLayerItem: {},
      vehicleEventType: true,
      roadNetLayerType: 'slippery',
      segmentInfo: {},
      slipperyPoint: 9,
      lowAttachmentPoint: 7,
      drawer: false,
      drawer_ani: 'panel-in-ani',
      is_show_right: true,
      timer: null,
      alarmList: [],
      //告警数据
      chartData: [],
      //图表数据
      sensorItem: 1,
      chartType: 'airTemperature',
      sensorData: {},
      //roadinfo 传感器数据
      rcsPos: 'rcs-service-right',
      layerPos: 'layer-left',
      roadInfoAndAlarmPos: 'road-info-and-alarm',
      rcsAndEventPos: 'rcs-event',
      meteorologicalType: [],
      //气象数
      // 据状态
      rainPointData: [],
      //
      roadsideIndex: 1,
      isShowColor: false,
      // webSocketMessage:{},
      sseTimer: null,
      baseUrl: "/spring/v1/",
      env: "production ",
      fastTimer: 0,
      rcsData: {
        //sirs 服务统计数据
        coveredArea: 0,
        coveredRoadLength: 0,
        totalMilage: 0,
        weather: {
          data: {
            "text": ""
          }
        }
      },
      summaryEventData: {
        num_waterroad: 0,
        num_wetroad: 0,
        num_bumpyroad: 0,
        waterRoadNameArray: [],
        slipperyRoadArray: [],
        bumpyRoadArray: []
      },
      roadsideEventColor: "black_font_color",
      //外面事件弹出框颜色
      adaptationHighly: '',
      outXidongEventArray: [],
      isShowOutEvent: false,
      getEventTime: "",
      sseTimeNow: "",
      outEventData: {
        plateNum: "",
        outsideEvent: "",
        outsideEventTime: "",
        coordinates: []
      }
    };
  },

  watch: {
    selectBur: function (val) {
      this.getAlarmList(val);
      this.getSensorDate(val);
      this.getLast24hSensorData(val); // this.getEventDate(this.selectBur)
    }
  },
  computed: { ...(0,mapState)(['selectBur'])
  },

  mounted() {
    this.componentsMoved();

    if (this.timer) {
      clearInterval(this.timer);
    } else {
      this.timer = setInterval(() => {
        setTimeout(() => {
          if (this.roadsideIndex != this.sensorItem) {
            this.mapClickSensor(this.roadsideIndex);
          }

          this.getSensorDate();
          this.getLast24hSensorData();
          this.getCoveredData();
        }, 0);
      }, 15 * 60 * 1000);
    }

    this.changeNormalLayerType({
      type: 2,
      check: true
    });
  },

  created() {
    this.initSSE();
    this.getAlarmList(this.selectBur);

    if (this.roadsideIndex != this.sensorItem) {
      this.mapClickSensor(this.roadsideIndex);
    }

    this.getSensorDate();
    this.getLast24hSensorData();
    this.getCoveredData();
  },

  beforeDestroy() {
    clearInterval(this.timer);
  },

  methods: {
    initSSE() {
      if ("EventSource" in window) {
        clearInterval(this.sseTimer);
        const that = this; //虽然 HTTP/1.1 的 SSE 默认不缓存，但加一个时间戳作为 URL 参数可以强制绕过浏览器/代理的缓存机制。

        let url = this.baseUrl + "stream_data?t=" + new Date().getTime(); // 防止缓存

        if (this.env == 'development') {
          url = this.baseUrl + "/stream_data";
        }

        let source = new EventSource(url);
        source.addEventListener('message', e => {
          if (e.data != 'None') {
            console.log(e.data);
            let jsonObj = JSON.parse(e.data);
            console.log(jsonObj);

            if (jsonObj != null) {
              if (jsonObj.eventType && that.selectBur == 1) {
                console.log(jsonObj);

                switch (jsonObj.eventType) {
                  case 'bump':
                    that.$refs.roadMap && that.$refs.roadMap.roadCamberEvent(jsonObj);
                    break;

                  case 'slip':
                    that.$refs.roadMap && that.$refs.roadMap.slipperyPointEvent(jsonObj);
                    break;

                  case 'ponding':
                    that.$refs.roadMap && that.$refs.roadMap.waterPointEvent(jsonObj);
                    break;

                  case 'ice':
                    that.$refs.roadMap && that.$refs.roadMap.icePointEvent(jsonObj);
                    break;

                  case 'low_attachment':
                    that.$refs.roadMap && that.$refs.roadMap.lowAttachmentPointEvent(jsonObj);
                    break;
                }

                this.getAlarmList(this.selectBur);
              }

              if (jsonObj.date_time) {
                that.$refs.roadMap && that.$refs.roadMap.getServerTime(jsonObj.date_time);
                this.changeOutEvent(jsonObj.date_time);
              }
            }
          }
        }, false); //底辅佐系数 和湿滑一样图标
        //结冰和积水同一个图标
        // 连接异常时会触发 error 事件并自动重连

        source.addEventListener('error', e => {
          if (e.target.readyState === EventSource.CLOSED) {
            if (this.sseTimer) {
              clearInterval(this.sseTimer);
            }

            this.sseTimer = setInterval(() => {
              this.initSSE();
            }, 15 * 1000);
          } else if (e.target.readyState === EventSource.CONNECTING) {
            console.log('Connecting...');
            clearInterval(this.sseTimer);
          }
        }, false);
      } else {
        console.log("浏览器不支持sse!");
      }
    },

    // initWebSocket(){
    //   // let client_id = Date.now()
    //   connectWebsocket(
    //       // 测试地址
    //       // `wss://sris-wuxi.bosch-mobility-solutions.cn/fastapi/${client_id}/ws_test`,
    //       `wss://sris-wuxi.bosch-mobility-solutions.cn/fastapi/ws_test`,
    //       // 传递给后台的数据
    //       {},
    //       // 成功拿到后台返回的数据的回调函数
    //       (data) => {
    //         console.log('成功的回调函数, 接收到的data数据： ', data)
    //         this.webSocketMessage = data
    //       },
    //       // websocket连接失败的回调函数
    //       () => {
    //         console.log('失败的回调函数')
    //       }
    //   );
    // },
    changeOutEvent(time) {
      if (this.getEventTime == "") {
        return;
      }

      let time1 = new Date(time).getTime();
      let time2 = new Date(this.getEventTime).getTime();
      let minute = ((time1 - time2) / 1000).toFixed(0);

      if (minute > 10) {
        this.isShowOutEvent = false;
        this.getEventTime = "";
        this.outEventData = {};
      }
    },

    componentsMoved() {
      let width = document.documentElement.clientWidth;
      let height = document.documentElement.clientHeight;
      this.adaptationHighly = height * 0.945 + 'px';

      if (width / height < 3.5) {
        this.rcsPos = 'rcs-server-drawer';
        this.layerPos = 'layer-drawer';
        this.roadInfoAndAlarmPos = 'road-info-and-alarm-drawer';
        this.rcsAndEventPos = 'rcs-event-drawer';
      } else {
        this.rcsPos = 'rcs-service-right';
        this.layerPos = 'layer-left';
        this.roadInfoAndAlarmPos = 'road-info-and-alarm';
        this.rcsAndEventPos = 'rcs-event';
      }
    },

    //切换图表类型
    changeType(val) {
      this.chartType = val;
      this.getLast24hSensorData();
    },

    //切换传感器
    changeSensor(val) {
      this.sensorItem = val; // if(val == 3 || val == 4){
      //   this.chartType = 'pondingDepth'
      // }else {
      //   this.chartType = 'airTemperature'
      // }

      this.chartType = 'airTemperature';
      this.getSensorDate();
      this.getLast24hSensorData();
    },

    //地图上点击传感器
    mapClickSensor(val) {
      this.roadsideIndex = val;
      http.post("get_real_time_sensor_data", {
        road_name: val
      }).then(res => {
        if (res.data) {
          this.$store.commit("SET_SENSORDATA", res.data);
        }
      });
    },

    //告警传感器数据
    getAlarmList(hour) {
      http.post('get-alarm-list', {
        hour: hour
      }).then(res => {
        if (res.data == null) {
          this.alarmList = [];
        } else {
          this.alarmList = res.data;
        }
      });
      this.getEventSummary();
    },

    //历史24小时数据
    getSensorDate() {
      http.post("get_real_time_sensor_data", {
        road_name: this.sensorItem
      }).then(res => {
        if (res.data) {
          this.sensorData = res.data;
        }
      });
    },

    //历史24小时图表数据
    getLast24hSensorData() {
      http.post("get_last24h_data_plot", {
        road_name: this.sensorItem,
        data_title: this.chartType
      }).then(res => {
        if (res.data) {
          this.chartData = res.data;
        }
      });
    },

    panelBtn() {
      this.drawer = true;
    },

    backHome() {
      console.log("back home"); //window.location.href = 'http://viz.wx-iov.com:8003/#/equipmentMap';

      window.location.href = 'http://192.168.42.22:8003/#/equipmentMap';
    },

    closeBtn() {
      this.drawer = false;
    },

    // back(){
    //  //   window.parent.postMessage('关闭', '*')
    //  window.location='http://36.137.74.133:9001/pages/main.html?type=1'
    // },
    // 切换车辆图层
    changeNormalLayerType(type) {
      this.normalLayerType = type;
    },

    // 切换路网图层
    changeRoadNetLayer(item) {
      if (item[0].check) {
        this.isShowColor = true;
        this.roadsideEventColor = "white_font_color";
      } else {
        this.isShowColor = false;
        this.roadsideEventColor = "black_font_color";
      }

      this.roadNetLayerItem = item;
      this.roadNetLayerType = item[0].type;
    },

    //切换气象数据
    changeMeteorologicalType(type) {
      this.meteorologicalType = type;
    },

    // 更新路况信息 （点击地图把数据传给 road-info）
    setRoadCondition(info) {
      this.segmentInfo = info;
    },

    // 更新告警列表
    deleteEvent(val) {
      this.getAlarmList(this.selectBur);
    },

    changeVehicleEventType(item) {
      console.log(item);
      this.vehicleEventType = item;
    },

    getCoveredData() {
      http.post("get_covered_range").then(res => {
        if (res.data) {
          this.rcsData.coveredArea = res.data[0];
          this.rcsData.coveredRoadLength = res.data[1];
          this.rcsData.totalMilage = res.data[2];
        }
      });
      http.get("get_weather").then(res => {
        console.log("get weather data");

        if (res.data) {
          console.log(res.data);
          this.rcsData.weather = res.data;
        }
      });
    },

    getEventSummary() {
      http.post("get-event-summary").then(res => {
        if (res.data) {
          this.summaryEventData.num_waterroad = res.data.water_road_amount;
          this.summaryEventData.num_wetroad = res.data.slippery_road_amount;
          this.summaryEventData.num_bumpyroad = res.data.bumpy_road_amount;
          this.summaryEventData.waterRoadNameArray = res.data.water_road_to_maintain;
          this.summaryEventData.slipperyRoadArray = res.data.slippery_road_to_maintain;
          this.summaryEventData.bumpyRoadArray = res.data.bumpy_road_to_maintain;
        } else {
          this.summaryEventData.waterRoadNameArray = [];
          this.summaryEventData.slipperyRoadArray = [];
          this.summaryEventData.bumpyRoadArray = [];
        }
      });
    },

    closeSlide() {
      this.slideIsShow = false;
    },

    //绑定锡东外事件赋值
    outXidongEventData(carId, ts, event, gps) {
      if (!this.vehicleEventType) {
        return;
      }

      switch (event) {
        case 'bumpy_event':
          this.outEventData.outsideEvent = '颠簸点';
          break;

        case 'slippery_event':
          this.outEventData.outsideEvent = '湿滑点';
          break;

        case 'ponding_event':
          this.outEventData.outsideEvent = '积水点';
          break;

        case 'ice_event':
          this.outEventData.outsideEvent = '结冰点';
          break;

        case 'low_attachment_event':
          this.outEventData.outsideEvent = '低附着系数点';
          break;
      }

      this.outEventData.plateNum = carId;
      this.outEventData.outsideEventTime = this.$moment(ts).format('YYYY-MM-DD HH:mm:ss');
      this.outEventData.coordinates = gps;
      this.isShowOutEvent = true;
      this.getEventTime = new Date();
    },

    // 道路湿滑事件
    // setSlipperyPoint(){
    //     setInterval(() => {
    //         this.slipperyPoint++;
    //         // this.notify();
    //     }, random(5, 15) * 1000)
    // },
    // // 道路低附着点事件
    // setLowAttachmentPoint(){
    //     setInterval(() => {
    //         this.lowAttachmentPoint++;
    //         this.notify();
    //     }, random(5, 15) * 1000)
    // },
    // 上报提示
    notify() {
      const date = new Date();
      const h = date.getHours(); //获取小时

      const m = date.getMinutes(); //获取分钟

      const s = date.getSeconds(); //获取秒

      const time = this.timeFormat(h) + ':' + this.timeFormat(m) + ':' + this.timeFormat(s);
      this.$notify({
        title: '上报成功！',
        type: 'success',
        message: time + '已上报至v2x应用平台！',
        offset: 400,
        duration: 3000
      });
    },

    timeFormat(time) {
      if (time < 10) {
        return '0' + time;
      } else {
        return time;
      }
    }

  }
})
</script>
