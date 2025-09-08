<template>
  <div class="auth-container">
    <a-form
      :model="formState"
      name="forgot"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      :disabled="loading"
      class="auth-form"
    >

      <h1 class="auth-title">Recover password</h1>

      <a-form-item name="email" :rules="[{ required: true, type:'email', message: 'Enter valid email' }]" :wrapper-col="{ span: 24 }">
        <a-input v-model:value="formState.email" placeholder="Email"/>
      </a-form-item>

      <a-form-item :wrapper-col="{ span: 24 }">
        <a-button type="primary" html-type="submit" :loading="loading" block>Send Reset Link</a-button>
      </a-form-item>

      <div class="muted">
        Remember your password?
        <a-typography-link @click="onBack">Back to Sign in</a-typography-link>
      </div>
    </a-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';

const formState = reactive({ email: '' });
const loading = ref(false);

const onFinish = async (values) => {
  loading.value = true;
  try {
    // mock api
    setTimeout(() => {
      message.success(`Recovery link sent to ${values.email} (mock)`);
      loading.value = false;
    }, 1000);
  } catch (e) {
    message.error(e?.message || 'Failed to send recovery link');
    loading.value = false;
  }
}

const onFinishFailed = () => message.error('Please check the form');
const onBack = () => window.location.href = '/login';
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}

.auth-form {
  width: 450px;
  padding: 36px 32px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.12);
}

.auth-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.muted {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0 6px;
}

</style>
