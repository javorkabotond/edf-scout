<template>
  <div class="p-4">
    <h1 class="text-2xl font-bold mb-4">EDF Files</h1>
    <table class="min-w-full border border-gray-300">
      <thead class="bg-gray-100">
      <tr>
        <th class="px-4 py-2 text-left">File Name</th>
        <th class="px-4 py-2 text-left">Recording Date</th>
        <th class="px-4 py-2 text-left">Patient Name</th>
        <th class="px-4 py-2 text-left">Channels</th>
        <th class="px-4 py-2 text-left">Annotations</th>
        <th class="px-4 py-2 text-left">Status</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="file in sortedFiles" :key="file.fileName" :class="file.valid ? '' : 'bg-red-100'">
        <td class="px-4 py-2">{{ file.fileName }}</td>
        <td class="px-4 py-2">{{ file.recordingDate ? new Date(file.recordingDate).toLocaleString() : '-' }}</td>
        <td class="px-4 py-2">{{ file.patientName || '-' }}</td>
        <td class="px-4 py-2">{{ file.numberOfChannels || '-' }}</td>
        <td class="px-4 py-2">{{ file.numberOfAnnotations || '-' }}</td>
        <td class="px-4 py-2">
          <span v-if="file.valid" class="text-green-600 font-semibold">Valid</span>
          <span v-else class="text-red-600 font-semibold">Invalid</span>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const files = ref([]);

const sortedFiles = computed(() => {
  return [...files.value].sort((a, b) => {
    if (!a.valid) return 1;
    if (!b.valid) return -1;
    return new Date(a.recordingDate) - new Date(b.recordingDate);
  });
});

onMounted(async () => {
  try {
    const res = await fetch('/api/files');
    files.value = await res.json();
  } catch (err) {
    console.error('Failed to fetch EDF files:', err);
  }
});
</script>

<style scoped>
table {
  border-collapse: collapse;
}
th, td {
  border: 1px solid #ccc;
}
</style>
