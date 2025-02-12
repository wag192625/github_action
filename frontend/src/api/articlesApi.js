import api from './axios';

const articlesApi = {
  getArticles: async () => {
    const resposne = await api.get('articles');
    return resposne.data;
  },
  postArticle: async (data) => {
    const resposne = await api.post(`articles`, data, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return resposne.data;
  },
  getArticle: async (id) => {
    const response = await api.get(`articles/${id}`);
    return response.data;
  },
};

export default articlesApi;
