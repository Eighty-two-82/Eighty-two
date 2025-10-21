import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  base: './',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  test: {
    root: '.', // 从项目根目录开始扫描
    globals: true,
    environment: 'jsdom',
    include: ['./tests/**/*.test.js'], // 明确指定你的测试目录
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
    },
  },
})
