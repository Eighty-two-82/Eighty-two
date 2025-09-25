# Daily Management API Documentation

## Overview
This API supports the Daily Management frontend interface for scheduling workers and managing their shifts.

## API Endpoints

### 1. Get Available Workers
**GET** `/api/workers/organization/{organizationId}/available`

Returns all active workers available for scheduling.

**Response:**
```json
{
  "code": "200",
  "message": "Available workers retrieved successfully!",
  "data": [
    {
      "id": "worker_id",
      "name": "Worker Name",
      "email": "worker@example.com",
      "workerId": "W001",
      "status": "active",
      "photoUrl": "https://example.com/photo.jpg"
    }
  ]
}
```

### 2. Create Daily Schedule
**POST** `/api/workers/daily-schedule`
**Headers:** `X-Manager-Id: manager_id`

Creates a daily schedule for multiple workers with morning and evening shifts.

**Request Body:**
```json
{
  "scheduleDate": "2025-09-23",
  "selectedWorkerIds": ["worker1", "worker2"],
  "morningShiftWorkerIds": ["worker1"],
  "eveningShiftWorkerIds": ["worker2"],
  "scheduleNotes": "Special instructions for today"
}
```

**Response:**
```json
{
  "code": "200",
  "message": "Daily schedule created successfully!",
  "data": [
    {
      "id": "worker_id",
      "name": "Worker Name",
      "shiftAllocations": [
        {
          "shiftDate": "2025-09-23",
          "shiftTime": "08:00-16:00",
          "status": "scheduled",
          "allocatedBy": "manager_id",
          "notes": "Special instructions for today"
        }
      ]
    }
  ]
}
```

### 3. Get Daily Schedule
**GET** `/api/workers/organization/{organizationId}/daily-schedule/{date}`

Retrieves the daily schedule for a specific date.

**Response:**
```json
{
  "code": "200",
  "message": "Daily schedule retrieved successfully!",
  "data": [
    {
      "id": "worker_id",
      "name": "Worker Name",
      "shiftAllocations": [
        {
          "shiftDate": "2025-09-23",
          "shiftTime": "08:00-16:00",
          "status": "scheduled",
          "allocatedBy": "manager_id"
        }
      ]
    }
  ]
}
```

### 4. Clear Daily Schedule
**DELETE** `/api/workers/organization/{organizationId}/daily-schedule/{date}`

Removes all shift allocations for a specific date.

**Response:**
```json
{
  "code": "200",
  "message": "Daily schedule cleared successfully!",
  "data": "Cleared 5 worker schedules"
}
```

### 5. Upload Worker Photo
**POST** `/api/workers/upload-photo`

Uploads a photo for a worker.

**Request Body:**
```json
{
  "workerId": "worker_id",
  "photoUrl": "https://example.com/uploaded-photo.jpg",
  "photoFileName": "worker_photo.jpg",
  "photoContentType": "image/jpeg",
  "photoSize": 1024000
}
```

**Response:**
```json
{
  "code": "200",
  "message": "Worker photo uploaded successfully!",
  "data": {
    "id": "worker_id",
    "name": "Worker Name",
    "photoUrl": "https://example.com/uploaded-photo.jpg"
  }
}
```

### 6. Worker Registration
**POST** `/api/workers/register`

Registers a new worker using an invite code.

**Request Body:**
```json
{
  "inviteCode": "ABC12345",
  "name": "Worker Name",
  "email": "worker@example.com",
  "phone": "+1234567890",
  "password": "secure_password"
}
```

**Response:**
```json
{
  "code": "200",
  "message": "Worker registered successfully!",
  "data": {
    "id": "worker_id",
    "name": "Worker Name",
    "email": "worker@example.com",
    "status": "active",
    "inviteCodeUsed": true
  }
}
```

## Frontend Integration

### Daily Management Modal Flow:

1. **Load Available Workers**: Call `GET /api/workers/organization/{orgId}/available`
2. **Select Date**: User selects date in date picker
3. **Select Workers**: User checks workers from the list
4. **Upload Photos**: For each selected worker, call `POST /api/workers/upload-photo`
5. **Assign Shifts**: User assigns workers to morning (8:00-16:00) or evening (16:00-24:00) shifts
6. **Create Schedule**: Call `POST /api/workers/daily-schedule` with all selections
7. **View Schedule**: Call `GET /api/workers/organization/{orgId}/daily-schedule/{date}` to display

### Data Flow:
- Frontend sends `DailyScheduleRequest` with selected workers and shift assignments
- Backend creates `ShiftAllocation` objects for each worker
- Each shift allocation includes date, time, manager ID, and notes
- Photos are uploaded separately and linked to worker records

## Error Handling

All endpoints return standardized error responses:

```json
{
  "code": "400",
  "message": "Error description",
  "data": null
}
```

Common error codes:
- `400`: Bad Request (missing required fields)
- `404`: Not Found (worker not found)
- `500`: Internal Server Error
