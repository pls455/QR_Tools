export const state = {
  role: null,
  editing: {
    branch: null,
    subject: null,
    category: null,
    resource: null,
    foundation: null
  },
  data: {
    branches: [],
    subjects: [],
    categories: [],
    resources: [],
    foundations: [],
    suggestions: [],
    logs: [],
    admins: [],
    templates: []
  }
};

export const cache = name => state.data[name] || [];
