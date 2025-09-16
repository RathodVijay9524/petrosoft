        package com.vijay.petrosoft.dto;
        import lombok.*;
        
        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        public class AttachmentDTO {
            private Long id;
            private String filename;
            private String contentType;
            private Long size;
            private String storagePath;
            private Long referencedEntityId;
            private String referencedEntityType;
        }
    
