<template>
  <div class="container">
    <h1 class="title">EDF File Browser</h1>

    <div v-if="loading">Loading files...</div>
    <div v-else>
      <div v-if="files.length === 0">No EDF files found.</div>

      <table class="edf-table">
        <thead>
        <tr>
          <th>File Name</th>
          <th>Identifier</th>
          <th>Recording Date</th>
          <th>Patient Name</th>
          <th>Number of Channels</th>
          <th>Channels</th>
          <th>Recording Length (s)</th>
          <th>Annotations</th>
          <th>Status</th>
          <th>Error Message</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="file in sortedFiles" :key="file.fileName">
          <td>{{ file.fileName }}</td>
          <td>{{ file.identifier || '—' }}</td>
          <td>{{ formatDate(file.recordingDate) }}</td>
          <td>{{ file.patientName || '—' }}</td>
          <td>{{ file.numberOfChannels }}</td>
          <td>
            <select>
              <option v-for="(ch, index) in file.channelNamesAndTypes" :key="index">{{ ch }}</option>
            </select>
          </td>
          <td>{{ file.recordingLengthSeconds }}</td>
          <td>{{ file.numberOfAnnotations }}</td>
          <td>{{ file.valid ? 'Valid' : 'Invalid' }}</td>
          <td>{{ file.errorMessage || '—' }}</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const files = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const res = await fetch('/api/files')
    files.value = await res.json()
  } catch (err) {
    console.error('Error fetching files:', err)
  } finally {
    loading.value = false
  }
})

const sortedFiles = computed(() => {
  return [...files.value].sort((a, b) => {
    const da = new Date(a.recordingDate || 0).getTime()
    const db = new Date(b.recordingDate || 0).getTime()
    return db - da
  })
})

const formatDate = (date) => {
  if (!date) return '—'
  const d = new Date(date)
  return d.toLocaleString()
}
</script>

<style>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  font-family: Arial, sans-serif;
}
.title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: #fff;
}
.edf-table {
  width: 100%;
  border-collapse: collapse;
}
.edf-table th, .edf-table td {
  border: 1px solid #ddd;
  padding: 0.5rem;
  vertical-align: top;
}
.edf-table th {
  background-color: #110a0a;
  color: #fff;
  text-align: left;
}
select {
  width: 100%;
}
</style>