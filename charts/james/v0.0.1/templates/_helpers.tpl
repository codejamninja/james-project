{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "james.name" }}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this
(by the DNS naming spec).
*/}}
{{- define "james.fullname" }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a name shared accross all apps in namespace.
We truncate at 63 chars because some Kubernetes name fields are limited to this
(by the DNS naming spec).
*/}}
{{- define "james.sharedname" }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- printf "%s-%s" .Release.Namespace $name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Calculate james certificate
*/}}
{{- define "james.james-certificate" }}
{{- if (not (empty .Values.ingress.james.certificate)) }}
{{- printf .Values.ingress.james.certificate }}
{{- else }}
{{- printf "%s-james-letsencrypt" (include "james.fullname" .) }}
{{- end }}
{{- end }}

{{/*
Calculate kibana certificate
*/}}
{{- define "james.kibana-certificate" }}
{{- if (not (empty .Values.ingress.kibana.certificate)) }}
{{- printf .Values.ingress.kibana.certificate }}
{{- else }}
{{- printf "%s-kibana-letsencrypt" (include "james.fullname" .) }}
{{- end }}
{{- end }}

{{/*
Calculate james hostname
*/}}
{{- define "james.james-hostname" }}
{{- if (and .Values.config.james.hostname (not (empty .Values.config.james.hostname))) }}
{{- printf .Values.config.james.hostname }}
{{- else }}
{{- if .Values.ingress.james.enabled }}
{{- printf .Values.ingress.james.hostname }}
{{- else }}
{{- printf "%s-james" (include "james.fullname" .) }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Calculate james base url
*/}}
{{- define "james.james-base-url" }}
{{- if (and .Values.config.james.baseUrl (not (empty .Values.config.james.baseUrl))) }}
{{- printf .Values.config.james.baseUrl }}
{{- else }}
{{- if .Values.ingress.james.enabled }}
{{- $hostname := ((empty (include "james.james-hostname" .)) | ternary .Values.ingress.james.hostname (include "james.james-hostname" .)) }}
{{- $path := (eq .Values.ingress.james.path "/" | ternary "" .Values.ingress.james.path) }}
{{- $protocol := (.Values.ingress.james.tls | ternary "https" "http") }}
{{- printf "%s://%s%s" $protocol $hostname $path }}
{{- else }}
{{- printf "http://%s" (include "james.james-hostname" .) }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Calculate cassandra hostname
*/}}
{{- define "james.cassandra-hostname" }}
{{- if (and .Values.config.cassandra.hostname (not (empty .Values.config.cassandra.hostname))) }}
{{- printf .Values.config.cassandra.hostname }}
{{- else }}
{{- printf "%s-cassandra" (include "james.fullname" .) }}
{{- end }}
{{- end }}

{{/*
Calculate tika hostname
*/}}
{{- define "james.tika-hostname" }}
{{- if (and .Values.config.tika.hostname (not (empty .Values.config.tika.hostname))) }}
{{- printf .Values.config.tika.hostname }}
{{- else }}
{{- printf "%s-tika" (include "james.fullname" .) }}
{{- end }}
{{- end }}
