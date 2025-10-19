import { describe, it, expect, vi, beforeEach } from 'vitest'
import * as notificationService from '@/services/notificationService'
import api from '@/services/api'

vi.mock('@/services/api')

describe('notificationService.js', () => {
  let mockSessionStorage

  beforeEach(() => {
    vi.clearAllMocks()
    mockSessionStorage = (() => {
      let store = {}
      return {
        getItem: vi.fn((key) => store[key]),
        setItem: vi.fn((key, value) => { store[key] = value }),
        removeItem: vi.fn((key) => delete store[key]),
        clear: vi.fn(() => { store = {} })
      }
    })()
    Object.defineProperty(window, 'sessionStorage', { value: mockSessionStorage })
  })

  // ---------- getMyNotifications ----------
  it('should get notifications successfully', async () => {
    sessionStorage.setItem('userId', 'u001')
    api.get.mockResolvedValueOnce({
      data: {
        code: '0',
        data: [
          { id: 1, title: 'New task', priority: 'high', createdAt: '2025-10-19T01:00:00Z', isRead: false }
        ]
      }
    })

    const result = await notificationService.getMyNotifications()
    expect(api.get).toHaveBeenCalledWith('/notifications/recipient/u001')
    expect(result.data[0]).toHaveProperty('title', 'New task')
    expect(result.data[0]).toHaveProperty('type', 'warning')
  })

  it('should return empty list when no userId', async () => {
    const result = await notificationService.getMyNotifications()
    expect(result.data).toEqual([])
  })

  it('should handle getMyNotifications error gracefully', async () => {
    sessionStorage.setItem('userId', 'u001')
    api.get.mockRejectedValueOnce(new Error('Network error'))
    const result = await notificationService.getMyNotifications()
    expect(result.data).toEqual([])
  })

  // ---------- getUnreadCount ----------
  it('should get unread count successfully', async () => {
    sessionStorage.setItem('userId', 'u001')
    api.get.mockResolvedValueOnce({ data: { code: '0', data: 5 } })

    const result = await notificationService.getUnreadCount()
    expect(api.get).toHaveBeenCalledWith('/notifications/unread/count', {
      headers: { 'X-User-Id': 'u001' }
    })
    expect(result.data).toBe(5)
  })

  it('should return 0 if no userId', async () => {
    const result = await notificationService.getUnreadCount()
    expect(result.data).toBe(0)
  })

  it('should return 0 when request fails', async () => {
    sessionStorage.setItem('userId', 'u001')
    api.get.mockRejectedValueOnce(new Error('Server error'))
    const result = await notificationService.getUnreadCount()
    expect(result.data).toBe(0)
  })

  // ---------- markNotificationAsRead ----------
  it('should mark notification as read successfully', async () => {
    api.put.mockResolvedValueOnce({ data: { code: '0', data: { success: true } } })
    const result = await notificationService.markNotificationAsRead('n123')
    expect(api.put).toHaveBeenCalledWith('/notifications/n123/read')
    expect(result.success).toBe(true)
  })

  it('should throw error when markNotificationAsRead fails', async () => {
    api.put.mockRejectedValueOnce({ response: { data: { msg: 'Not found' } } })
    await expect(notificationService.markNotificationAsRead('bad')).rejects.toThrow('Not found')
  })

  // ---------- markAllNotificationsAsRead ----------
  it('should mark all notifications as read successfully', async () => {
    sessionStorage.setItem('userId', 'u001')
    api.put.mockResolvedValueOnce({ data: { code: '0', data: { success: true } } })

    const result = await notificationService.markAllNotificationsAsRead()
    expect(api.put).toHaveBeenCalledWith('/notifications/read-all', {}, {
      headers: { 'X-User-Id': 'u001' }
    })
    expect(result.success).toBe(true)
  })

  it('should throw error when no userId in markAllNotificationsAsRead', async () => {
    await expect(notificationService.markAllNotificationsAsRead()).rejects.toThrow('User not authenticated')
  })

  // ---------- createNotification ----------
  it('should create notification successfully', async () => {
    api.post.mockResolvedValueOnce({ data: { code: '0', data: { id: 1 } } })
    const result = await notificationService.createNotification({ title: 'New message' })
    expect(api.post).toHaveBeenCalledWith('/notifications', { title: 'New message' })
    expect(result.success).toBe(true)
  })

  it('should throw error when createNotification fails', async () => {
    api.post.mockRejectedValueOnce({ response: { data: { msg: 'Bad request' } } })
    await expect(notificationService.createNotification({})).rejects.toThrow('Bad request')
  })
})
