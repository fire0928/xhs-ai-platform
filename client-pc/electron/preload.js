const { contextBridge, ipcRenderer } = require('electron')

contextBridge.exposeInMainWorld('electronAPI', {
  getUserInfo: () => ipcRenderer.invoke('get-user-info'),
  isElectron: true
})
