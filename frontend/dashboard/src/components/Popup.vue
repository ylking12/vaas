<template>
  <Teleport to="body">
    <Transition name="popup-fade">
      <div v-if="modelValue" class="popup-mask" @click.self="onMaskClick" @keydown.esc="close">
        <div class="popup-container" :class="type" role="dialog" aria-modal="true">
          <!-- 标题栏 -->
          <div class="popup-header">
            <span class="popup-icon" :class="`icon-${type}`">{{ iconChar }}</span>
            <span class="popup-title">{{ title }}</span>
            <button class="popup-close" @click="close" aria-label="关闭">×</button>
          </div>

          <!-- 内容区 -->
          <div class="popup-body">
            <slot />
          </div>

          <!-- 底部按钮 -->
          <div v-if="$slots.footer || showDefaultButtons" class="popup-footer">
            <slot name="footer">
              <button v-if="showDefaultButtons" class="popup-btn popup-btn-cancel" @click="close">{{ cancelText }}</button>
              <button v-if="showDefaultButtons" class="popup-btn popup-btn-confirm" @click="onConfirm">{{ confirmText }}</button>
            </slot>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed, watch, onBeforeUnmount } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '提示' },
  type: { type: String, default: 'info' },  // info | success | warning | danger
  showDefaultButtons: { type: Boolean, default: true },
  confirmText: { type: String, default: '确定' },
  cancelText: { type: String, default: '取消' },
  maskClosable: { type: Boolean, default: true }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'cancel'])

const iconChar = computed(() => {
  return { info: 'i', success: '✓', warning: '!', danger: '✕' }[props.type] || 'i'
})

function close() {
  emit('update:modelValue', false)
  emit('cancel')
}

function onConfirm() {
  emit('confirm')
  emit('update:modelValue', false)
}

function onMaskClick() {
  if (props.maskClosable) close()
}

// 按 Esc 关闭
function onKeydown(e) {
  if (e.key === 'Escape' && props.modelValue) close()
}

watch(() => props.modelValue, (v) => {
  if (v) {
    document.addEventListener('keydown', onKeydown)
  } else {
    document.removeEventListener('keydown', onKeydown)
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', onKeydown)
})
</script>

<style scoped>
.popup-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.65);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.popup-container {
  min-width: 360px;
  max-width: 520px;
  background: #1a1a1a;
  border: 1px solid rgba(255, 246, 218, 0.2);
  border-radius: 6px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6);
  color: #FFF6DA;
  overflow: hidden;
}

.popup-container.info    { border-top: 3px solid #909399; }
.popup-container.success { border-top: 3px solid #67C23A; }
.popup-container.warning { border-top: 3px solid #E6A23C; }
.popup-container.danger  { border-top: 3px solid #F56C6C; }

.popup-header {
  display: flex;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(255, 246, 218, 0.15);
  background: rgba(255, 246, 218, 0.03);
}

.popup-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  margin-right: 10px;
  background: #909399;
  color: #1a1a1a;
}
.popup-icon.icon-info    { background: #909399; }
.popup-icon.icon-success { background: #67C23A; }
.popup-icon.icon-warning { background: #E6A23C; }
.popup-icon.icon-danger  { background: #F56C6C; }

.popup-title {
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  color: #FFF6DA;
  letter-spacing: 1px;
}

.popup-close {
  background: transparent;
  border: none;
  color: #a0a0a0;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  padding: 0 4px;
  transition: color 0.2s;
}
.popup-close:hover { color: #FFF6DA; }

.popup-body {
  padding: 20px 16px;
  font-size: 14px;
  line-height: 1.6;
  color: #FFF6DA;
  min-height: 60px;
}

.popup-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 246, 218, 0.15);
  background: rgba(0, 0, 0, 0.2);
}

.popup-btn {
  min-width: 72px;
  height: 32px;
  padding: 0 16px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.2s;
}

.popup-btn-cancel {
  background: transparent;
  border-color: rgba(255, 246, 218, 0.3);
  color: #c0d0e0;
}
.popup-btn-cancel:hover {
  border-color: #FFF6DA;
  color: #FFF6DA;
}

.popup-btn-confirm {
  background: linear-gradient(90deg, #32281e, #FFF6DA);
  color: #1a1a1a;
  font-weight: 500;
}
.popup-btn-confirm:hover {
  background: linear-gradient(90deg, #FFF6DA, #32281e);
  color: #000;
}

.popup-container.warning .popup-btn-confirm { background: linear-gradient(90deg, #5a3e1e, #E6A23C); color: #1a1a1a; }
.popup-container.danger  .popup-btn-confirm { background: linear-gradient(90deg, #5a1e1e, #F56C6C); color: #1a1a1a; }
.popup-container.success .popup-btn-confirm { background: linear-gradient(90deg, #1e3a1e, #67C23A); color: #1a1a1a; }

/* 过渡动画 */
.popup-fade-enter-active, .popup-fade-leave-active {
  transition: opacity 0.2s;
}
.popup-fade-enter-from, .popup-fade-leave-to {
  opacity: 0;
}
</style>
