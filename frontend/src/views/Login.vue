<template>
  <div class="login-container">

    <a-form
      :model="formState"
      name="Login"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      :disabled="loading"
      class="login-form"
    >

      <!-- heading -->
      <h1 class="login-title">Sign in</h1>

      <a-form-item
        name="Email"
        :rules="[{ required: true, message: 'Please input your Email!' }]"
        :wrapper-col="{ span: 24 }"
>
        <a-input v-model:value="formState.Email" placeholder="Enter Email">
          <template #prefix>
            <UserOutlined />
          </template>
        </a-input>
      </a-form-item>


      <a-form-item
        name="password"
        :rules="[{ required: true, message: 'Please input your password!' }]"
        :wrapper-col="{ span: 24 }">
        <a-input-password v-model:value="formState.password" placeholder="Enter password">
          <template #prefix>
            <LockOutlined />
          </template>
        </a-input-password>
      </a-form-item>


      <a-form-item :wrapper-col="{ span: 24 }">
        <div class="row-between">
          <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
          <a class="login-form-forgot" @click="onForgot">Forgot password?</a>
        </div>
      </a-form-item>



      <a-form-item :wrapper-col="{ span: 24 }">
        <a-button type="primary" html-type="submit" :loading="loading" block>Sign in</a-button>
      </a-form-item>

      <!-- sign up -->
      <div class="muted"> 
        Don't have an account? 
        <a-typography-link @click="onRegister">Register</a-typography-link>
      </div>
    </a-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();


const formState = reactive({
  username: '',
  password: '',
  remember: true,
  role: ''
});

const loading = ref(false);

function simulateLogin({ username, password }) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (username && password) {
        resolve({ token: 'fake-token-123' });
      } else {
        reject(new Error('Empty username or password'));
      }
    }, 1000);
  });
}

const onFinish = async (values) => {
  loading.value = true;
  try {
    const res = await simulateLogin(values);
    const token = res.token;
    if (formState.remember) {
      localStorage.setItem('token', token);
    }
    message.success('Signed in (mock)');
    window.location.href = '/';
  } catch (e) {
    message.error(e?.message || 'Mock login failed');
  } finally {
    loading.value = false;
  }
}

const onFinishFailed = (errorInfo) => {
  console.log('Failed:', errorInfo);
  message.error('Please check the form');
}

const onForgot = () => {
  router.push('/forgot-password');
}

const onRegister = () => {
  router.push('/register');
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;   
  align-items: center;       
  height: 100vh;             
  background: #f0f2f5;       
}

.login-form {
  width: 450px;
  padding: 36px 32px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.12);
}

.login-input {
  width: 320px;
  margin: 0 auto;
  display: block;
}


/* title formate */
.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.row-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.muted {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0 6px;
}
</style>
