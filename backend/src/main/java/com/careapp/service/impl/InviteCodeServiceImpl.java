package com.careapp.service.impl;

import com.careapp.domain.InviteCode;
import com.careapp.repository.InviteCodeRepository;
import com.careapp.service.InviteCodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class InviteCodeServiceImpl implements InviteCodeService {

    @Resource
    private InviteCodeRepository inviteCodeRepository;

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
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null) {
            return false;
        }
        
        // Check if code is used or expired
        return !inviteCode.isUsed() && inviteCode.getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean useInviteCode(String code, String usedBy) {
        InviteCode inviteCode = inviteCodeRepository.findByCode(code);
        if (inviteCode == null || inviteCode.isUsed() || inviteCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        inviteCode.setUsed(true);
        inviteCode.setUsedBy(usedBy);
        inviteCode.setUsedAt(LocalDateTime.now());
        inviteCodeRepository.save(inviteCode);
        
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
