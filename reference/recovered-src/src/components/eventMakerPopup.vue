var render = function render(){var _vm=this,_c=_vm._self._c;return _c('div',[_c('div',{staticClass:"event-popup"},[_c('div',{staticClass:"tit"},[_vm._v("是否删除该"+_vm._s(_vm.makerTitle)+"点？")]),_c('div',{staticClass:"info"},[_vm._v("纬度:"+_vm._s(_vm.lat))]),_c('div',{staticClass:"info"},[_vm._v("经度:"+_vm._s(_vm.lan))]),_c('div',{staticClass:"btn_div"},[_c('div',{staticClass:"btn",on:{"click":function($event){return _vm.clickBtn(true)}}},[_vm._v("确定")]),_c('div',{staticClass:"btn",on:{"click":function($event){return _vm.clickBtn(false)}}},[_vm._v("取消")])])])])
}
var staticRenderFns = []

export { render, staticRenderFns }