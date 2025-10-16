package com.careapp.service.impl;

import com.careapp.domain.InviteCode;
import com.careapp.repository.InviteCodeRepository;
import com.careapp.service.InviteCodeService;
import com.careapp.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class InviteCodeServiceImpl implements InviteCodeService {

    @Resource
    private InviteCodeRepository inviteCodeRepository;
    
    @Resource
    private UserService userService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    @Override
    public String generateInviteCode(String createdBy, String createdByType, String targetType, String patientId, String organizationId) {
        // Generate unique code
        String code = generateUniqueCode();
        
        // Create invite code
        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        inviteCode.setCreatedBy(createdBy);
        inviteCode.setCreatedByType(createdByType);
        inviteCode.setTargetType(targetType);
        inviteCode.setPatientId(patientId);
        inviteCode.setOrganizationId(organizationId);
        inviteCode.setCreatedAt(LocalDateTime.now());
        inviteCode.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7 days expiry
        inviteCode.setUsed(false);
        
        inviteCodeRepository.save(inviteCode);
        return code;
    }

    @Override
    public boolean validateInviteCode(String code) {
        System.out.println("🔍 Backend - validateInviteCode called with code: " + code);
        
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null) {
            System.out.println("❌ Backend - Invite code not found: " + code);
            return false;
        }
        
        System.out.println("🔍 Backend - Found invite code:");
        System.out.println("  - Code: " + inviteCode.getCode());
        System.out.println("  - Created: " + inviteCode.getCreatedAt());
        System.out.println("  - Expires: " + inviteCode.getExpiresAt());
        System.out.println("  - Is Used: " + inviteCode.isUsed());
        System.out.println("  - Current Time: " + LocalDateTime.now());
        System.out.println("  - Is Expired: " + inviteCode.getExpiresAt().isBefore(LocalDateTime.now()));
        
        // Only check if code is expired, not if it's used
        // The same invite code can be used by multiple users within the expiration period
        boolean isValid = inviteCode.getExpiresAt().isAfter(LocalDateTime.now());
        System.out.println("🔍 Backend - Invite code validation result: " + isValid);
        
        return isValid;
    }

    @Override
    public boolean useInviteCode(String code, String usedBy) {
        System.out.println("🔍 Backend - useInviteCode called with code: " + code + ", usedBy: " + usedBy);
        
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null) {
            System.out.println("❌ Backend - Invite code not found: " + code);
            return false;
        }
        
        if (inviteCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            System.out.println("❌ Backend - Invite code expired: " + code);
            return false;
        }
        
        System.out.println("✅ Backend - Invite code is valid, proceeding to use it");
        
        // Don't mark the invite code as used since it can be used by multiple users
        // Just mark the user as having used an invite code
        
        // Mark user as having used an invite code
        try {
            boolean markSuccess = userService.markUserAsUsedInviteCode(usedBy);
            if (markSuccess) {
                System.out.println("✅ Successfully marked user " + usedBy + " as having used invite code");
            } else {
                System.out.println("❌ Failed to mark user " + usedBy + " as having used invite code");
            }
        } catch (Exception e) {
            System.err.println("❌ Error marking user as having used invite code: " + e.getMessage());
            // Don't fail the invite code usage if marking fails
        }
        
        // If this is a MANAGER invite code, bind the manager to the patient
        if ("MANAGER".equals(inviteCode.getTargetType()) && inviteCode.getPatientId() != null) {
            try {
                // Bind the manager to the patient
                boolean bindSuccess = userService.bindManagerToPatient(usedBy, inviteCode.getPatientId());
                if (bindSuccess) {
                    System.out.println("✅ Successfully bound manager " + usedBy + " to patient " + inviteCode.getPatientId());
                    System.out.println("🔍 Manager can now access client information for patient: " + inviteCode.getPatientId());
                } else {
                    System.out.println("❌ Failed to bind manager " + usedBy + " to patient " + inviteCode.getPatientId());
                }
            } catch (Exception e) {
                System.err.println("❌ Error binding manager to patient: " + e.getMessage());
                // Don't fail the invite code usage if binding fails
            }
        }
        
        return true;
    }

    @Override
    public List<InviteCode> getInviteCodesByCreator(String creatorId) {
        return inviteCodeRepository.findByCreatedBy(creatorId);
    }

    @Override
    public boolean revokeInviteCode(String codeId) {
        try {
            inviteCodeRepository.deleteById(codeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<InviteCode> getActiveInviteCodesForPatient(String patientId) {
        return inviteCodeRepository.findByPatientIdAndIsUsedFalseAndExpiresAtAfter(patientId, LocalDateTime.now());
    }

    @Override
    public List<InviteCode> getInviteCodesUsedByUser(String userId) {
        return inviteCodeRepository.findByUsedBy(userId);
    }

    @Override
    public void cleanupExpiredInviteCodes() {
        List<InviteCode> expiredCodes = inviteCodeRepository.findByExpiresAtBeforeAndIsUsedFalse(LocalDateTime.now());
        inviteCodeRepository.deleteAll(expiredCodes);
    }

    private String generateUniqueCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        
        do {
            code.setLength(0);
            for (int i = 0; i < CODE_LENGTH; i++) {
                code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
        } while (inviteCodeRepository.findByCode(code.toString()) != null);
        
        return code.toString();
    }
}
