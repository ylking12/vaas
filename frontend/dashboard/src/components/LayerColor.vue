<!--
  LayerColor - 路网图层图例（右上角色条 + 刻度）
  还原自原版 layer-color.vue，根据当前路网图层类型显示对应图例：
    - slippery(干湿):  绿->黄->红   干燥/潮湿/积水
    - friction(附着):  绿->黄->红   1/0.5/0（两端配 friction-1.png / friction-0.png）
    - temperature(温度): 蓝->绿->黄红渐变  低温/正常/高温
  图例随路网图层选中状态显示/隐藏（由父组件 v-show 控制）。
-->
<template>
  <div class="layer-color-wrapper">
    <!--friction 附着系数-->
    <div class="item friction" v-show="roadNetLayerType === 'friction'">
      <h6>附着系数</h6>
      <div class="color"></div>
      <ul>
        <li>1</li>
        <li>0.5</li>
        <li>0</li>
      </ul>
      <img class="friction-1" :src="friction1Icon" alt="friction-1">
      <img class="friction-0" :src="friction0Icon" alt="friction-0">
    </div>
    <!--temperature 路面温度-->
    <div class="item temperature" v-show="roadNetLayerType === 'temperature'">
      <h6>路面温度</h6>
      <div class="color">
        <p></p>
        <p></p>
        <p></p>
      </div>
      <ul>
        <li>低温</li>
        <li>正常</li>
        <li>高温</li>
      </ul>
    </div>
    <!--slippery 干湿状态（当前还原版图层 key=dryWet，父组件映射为 slippery）-->
    <div class="item slippery" v-show="roadNetLayerType === 'slippery'">
      <h6>干湿状态</h6>
      <div class="color">
        <p></p>
        <p></p>
        <p></p>
      </div>
      <ul>
        <li>干燥</li>
        <li>潮湿</li>
        <li>积水</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
/** SOURCE: Recovered from source map (587.95c7fd57.js.map) | ORIGINAL: src/components/layer-color.vue | STATUS: Restored */
import friction1Icon from '@/assets/img/friction-1.png'
import friction0Icon from '@/assets/img/friction-0.png'

// roadNetLayerType: 'slippery' | 'friction' | 'temperature'
defineProps({
  roadNetLayerType: { type: String, default: '' }
})
</script>

<style scoped>
/* 还原原版 layer-color.scss（宽屏 @media min-aspect-ratio:7/2 分支，大屏主用）
   rem -> px（按 1920x1080 大屏视觉比例换算）*/
.layer-color-wrapper {
  position: absolute;
  right: 3%;
  top: 3%;
  z-index: 10;          /* 高于地图，低于 time-slider(999) 和 drawer */
  color: #fff;
  pointer-events: none;
}
.layer-color-wrapper .item h6 {
  font-size: 13px;
  color: #fff;
  font-weight: 400;
  margin: 0;
}
.layer-color-wrapper .item .color {
  width: 168px;
  height: 14px;
  margin: 6px 0 4px;
}
.layer-color-wrapper .item li {
  font-size: 12px;
  color: #fff;
  list-style: none;
}

/* friction 附着系数：色条整体渐变 + 两端配图 */
.layer-color-wrapper .friction { position: relative; }
.layer-color-wrapper .friction .color {
  background-image: linear-gradient(90deg, #16ac0d, #f2a207, #ff0e1f);
}
.layer-color-wrapper .friction ul { display: flex; padding: 0; margin: 0; }
.layer-color-wrapper .friction ul li { flex: 1; }
.layer-color-wrapper .friction ul li:first-child { text-align: left; }
.layer-color-wrapper .friction ul li:nth-child(2) { text-align: center; }
.layer-color-wrapper .friction ul li:nth-child(3) { text-align: right; }
.layer-color-wrapper .friction .friction-1 {
  position: absolute; top: 26px; left: -14px; width: 14px; height: 14px;
}
.layer-color-wrapper .friction .friction-0 {
  position: absolute; top: 26px; right: -14px; width: 14px; height: 14px;
}

/* slippery 干湿状态：3 段纯色 */
.layer-color-wrapper .slippery .color { display: flex; }
.layer-color-wrapper .slippery .color p { flex: 1; margin: 0; }
.layer-color-wrapper .slippery .color p:first-child { background: #5bb533; }
.layer-color-wrapper .slippery .color p:nth-child(2) { background: #f2a207; }
.layer-color-wrapper .slippery .color p:nth-child(3) { background: #e20622; }
.layer-color-wrapper .slippery ul { display: flex; padding: 0; margin: 0; }
.layer-color-wrapper .slippery ul li { flex: 1; text-align: center; }

/* temperature 路面温度：低温(蓝) -> 正常(绿) -> 高温(黄红渐变) */
.temperature .color { display: flex; }
.temperature .color p {
  flex: 1; margin: 0;
  background-image: linear-gradient(90deg, #ffea00, #f8a206, #f1590b, #ea1111);
}
.temperature .color p:first-child { background: blue; }
.temperature .color p:nth-child(2) { background: #5bb533; }
.temperature ul { display: flex; padding: 0; margin: 0; }
.temperature ul li { flex: 1; text-align: center; }
</style>
