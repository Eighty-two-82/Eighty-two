<template>
  <div class="auth-container">
    <a-form
      :model="formState"
      name="register"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      :disabled="loading"
      class="auth-form"
    >
      <h1 class="auth-title">Register</h1>

      <a-form-item name="firstName" :rules="[{ required: true, message: 'Enter first name' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> First name</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-input v-model:value="formState.firstName" placeholder="First name" style="flex: 1;"/>
          <a-tooltip title="Enter your first name">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item name="lastName" :rules="[{ required: true, message: 'Enter last name' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> Last name</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-input v-model:value="formState.lastName" placeholder="Last name" style="flex: 1;"/>
          <a-tooltip title="Enter your last name">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item name="email" :rules="[{ required: true, type:'email', message: 'Enter valid email' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> Email</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-input v-model:value="formState.email" placeholder="Email" style="flex: 1;"/>
          <a-tooltip title="Enter your email address">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item name="role" :rules="[{ required: true, message: 'Please select a role' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> Role</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-radio-group v-model:value="formState.role" style="flex: 1;">
            <a-radio value="poa">POA / Family Member</a-radio>
            <a-radio value="worker">Worker</a-radio>
            <a-radio value="manager">Manager</a-radio>
          </a-radio-group>
          <a-tooltip title="Select your role: POA/Family Member, Worker, or Manager">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item name="password" :rules="[{ required: true, message: 'Enter password' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> Password</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-input-password v-model:value="formState.password" placeholder="Password" style="flex: 1;"/>
          <a-tooltip title="Enter a secure password">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item name="confirm" :rules="[{ required: true, message: 'Confirm password' }]" style="display: flex; align-items: center;">
        <span style="margin-right: 16px; white-space: nowrap;"><span style="color: #ff4d4f;">*</span> Confirm Password</span>
        <div style="flex: 1; display: flex; align-items: center;">
          <a-input-password v-model:value="formState.confirm" placeholder="Confirm password" style="flex: 1;"/>
          <a-tooltip title="Re-enter your password to confirm">
            <span class="help-icon">?</span>
          </a-tooltip>
        </div>
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" :loading="loading" block>Register</a-button>
      </a-form-item>

      <div class="required-note">
        <span style="color: #ff4d4f;">*</span> Required fields
      </div>

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
  confirm: '',
  role: ''
});

const loading = ref(false);

const onFinish = async (values) => {
  loading.value = true;
  try {
    if (values.password !== values.confirm) {
      throw new Error('Passwords do not match');
    }
    console.log('Selected role:', values.role);
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
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}

.auth-form {
  width: 500px;
  padding: 28px 24px;
  background: none;
  border-radius: 0;
  box-shadow: none;
}

.auth-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.ant-input,
.ant-input-password {
  width: 100% !important;
}

.help-icon {
  color: #1890ff;
  cursor: pointer;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  border: 1px solid #1890ff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-left: 8px;
}

.required-note {
  text-align: center;
  color: #6b7280;
  font-size: 12px;
  margin-bottom: 16px;
}

.muted {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin-top: 12px;
}
</style>