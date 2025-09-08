<template>
  <div class="auth-container">
    <a-form
      :model="formState"
      name="register"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      :disabled="loading"
      class="auth-form"
    >

      <h1 class="auth-title">Register</h1>

      <a-form-item name="firstName" :rules="[{ required: true, message: 'Enter first name' }]" :wrapper-col="{ span: 24 }">
        <a-input v-model:value="formState.firstName" placeholder="First name"/>
      </a-form-item>

      <a-form-item name="lastName" :rules="[{ required: true, message: 'Enter last name' }]" :wrapper-col="{ span: 24 }">
        <a-input v-model:value="formState.lastName" placeholder="Last name"/>
      </a-form-item>

      <a-form-item name="email" :rules="[{ required: true, type:'email', message: 'Enter valid email' }]" :wrapper-col="{ span: 24 }">
        <a-input v-model:value="formState.email" placeholder="Email"/>
      </a-form-item>

      <a-form-item name="password" :rules="[{ required: true, message: 'Enter password' }]" :wrapper-col="{ span: 24 }">
        <a-input-password v-model:value="formState.password" placeholder="Password"/>
      </a-form-item>

      <a-form-item name="confirm" :rules="[{ required: true, message: 'Confirm password' }]" :wrapper-col="{ span: 24 }">
        <a-input-password v-model:value="formState.confirm" placeholder="Confirm password"/>
      </a-form-item>

      <a-form-item :wrapper-col="{ span: 24 }">
        <a-button type="primary" html-type="submit" :loading="loading" block>Register</a-button>
      </a-form-item>

      <div class="muted">
        Already have an account?
        <a-typography-link @click="onBack">Back to Sign in</a-typography-link>
      </div>
    </a-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';

const formState = reactive({
  firstName: '',
  lastName: '',
  email: '',
  password: '',
  confirm: ''
});

const loading = ref(false);

const onFinish = async (values) => {
  loading.value = true;
  try {
    if (values.password !== values.confirm) {
      throw new Error('Passwords do not match');
    }
    message.success('Registered successfully (mock)');
    window.location.href = '/login';
  } catch (e) {
    message.error(e?.message || 'Register failed');
  } finally {
    loading.value = false;
  }
}

const onFinishFailed = () => message.error('Please check the form');
const onBack = () => window.location.href = '/login';
</script>

<style scoped>
@import './auth.css';
</style>
