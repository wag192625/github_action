import axios from 'axios';

// const baseURL = `${import.meta.env.VITE_API_URL}`;
const baseURL = 'api/';
const instance = axios.create({
  baseURL,
});

export default instance;
