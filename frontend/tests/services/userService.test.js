import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as userService from '@/services/userService'
import api from '@/services/api'

// ✅ mock api.js（axios 封装）
vi.mock('@/services/api')

// ✅ mock sessionStorage
const mockSessionStorage = (() => {
  let store = {}
  return {
    getItem: vi.fn((key) => store[key]),
    setItem: vi.fn((key, value) => { store[key] = value }),
    removeItem: vi.fn((key) => delete store[key]),
    clear: vi.fn(() => { store = {} }),
  }
})()
Object.defineProperty(window, 'sessionStorage', {
  value: mockSessionStorage,
})

// ✅ mock window.location
delete window.location
window.location = { href: '' }

describe('userService.js', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    sessionStorage.clear()
  })

  it('should login successfully and return user info + token', async () => {
    api.post.mockResolvedValueOnce({
      data: {
        code: '0',
        data: { id: 'u123', email: 'test@example.com', firstName: 'Alice', lastName: 'Lee' }
      }
    })

    api.get.mockResolvedValueOnce({
      data: { code: '0', data: { valid: true, reason: 'active' } }
    })

    const result = await userService.login({ email: 'test@example.com', password: '123456' })

    expect(api.post).toHaveBeenCalledWith('/auth/login-email', {
      email: 'test@example.com',
      password: '123456'
    })
    expect(api.get).toHaveBeenCalledWith('/auth/invite-status?userId=u123')
    expect(result.data.user.email).toBe('test@example.com')
    expect(result.data.token).toMatch(/^token_/)
    expect(result.data.inviteStatus.valid).toBe(true)
    expect(sessionStorage.setItem).toHaveBeenCalledWith('userId', 'u123')
  })
})
