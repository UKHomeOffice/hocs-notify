{{- define "deployment.envs" }}
- name: JAVA_OPTS
  value: '{{ tpl .Values.app.env.javaOpts . }}'
- name: SERVER_PORT
  value: '{{ include "hocs-app.port" . }}'
- name: SPRING_PROFILES_ACTIVE
  value: '{{ tpl .Values.app.env.springProfiles . }}'
- name: AWS_SQS_NOTIFY_URL
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-notify-sqs
      key: sqs_queue_url
- name: AWS_SQS_ACCESS_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-notify-sqs
      key: access_key_id
- name: AWS_SQS_SECRET_KEY
  valueFrom:
    secretKeyRef:
      name: {{ .Release.Namespace }}-notify-sqs
      key: secret_access_key
- name: HOCS_INFO_SERVICE
  value: '{{ tpl .Values.app.env.infoService . }}'
- name: HOCS_BASICAUTH
  valueFrom:
    secretKeyRef:
      name: ui-casework-creds
      key: plaintext
- name: NOTIFY_APIKEY
  valueFrom:
    secretKeyRef:
      name: cs-notify
      key: api_key
- name: HOCS_URL
  valueFrom:
    secretKeyRef:
      name: cs-notify
      key: url
{{- end -}}
